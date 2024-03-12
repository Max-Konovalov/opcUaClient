package ru.brighttech.opcuaclient.domain.service;


import lombok.RequiredArgsConstructor;
import org.eclipse.milo.opcua.sdk.client.AddressSpace;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.brighttech.opcuaclient.domain.persistence.entity.Device;
import ru.brighttech.opcuaclient.domain.persistence.entity.DeviceData;
import ru.brighttech.opcuaclient.domain.persistence.repository.DeviceDataRepo;
import ru.brighttech.opcuaclient.domain.persistence.repository.DeviceRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;


@Component
@RequiredArgsConstructor
@EnableScheduling
public class OpcUaConnetor {

    private final DeviceRepo deviceRepo;
    private final DeviceDataRepo deviceDataRepo;
    private final ExecutorService executorService;


    public void showInnerNodes(OpcUaClient client, UaNode node, String inner) throws Exception {
            AttributeId attributeId = AttributeId.Value;
            inner = inner + "\t";
            AddressSpace addressSpace = client.getAddressSpace();
            List<? extends UaNode> nodes = addressSpace.browseNodes(node);
            for (UaNode node1 : nodes
            ) {

                if (node1 == null) continue;

                System.out.println(
                        inner + "|___" +
                                node1.getDisplayName().getText() + " : " +
                                node1.readAttribute(attributeId).getValue().getValue() + "id= " +
                                node1.getNodeId()
                );
                showInnerNodes(client, node1, inner);
            }
    }

    public void sphinxConnector(String endpointUrl, Integer namespace, String address) throws UaException, ExecutionException, InterruptedException {

        //Настройка
//        String endpointUrl = "opc.tcp://10.103.36.28:4840";

        //Создание клиента и подключение к серверу
        OpcUaClient client = OpcUaClient.create(
                endpointUrl,
                endpoints ->
                        endpoints.stream()
                                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                .findFirst(),
                OpcUaClientConfigBuilder::build
        );
        client.connect().get();

        ManagedSubscription subscription = ManagedSubscription.create(client, 10000.0);

        //Здесь вставь создание NodeId
        NodeId nodeId = new NodeId(namespace, address);

        // Создание ManagedDataItem для мониторинга атрибута датчика
        ManagedDataItem dataItem = subscription.createDataItem(nodeId);

        // Проверка статуса кода после создания ManagedDataItem
        if (!dataItem.getStatusCode().isGood()) {
            throw new RuntimeException("Ошибка при создании ManagedDataItem");
        }

        // Добавление слушателя изменений данных

        subscription.addChangeListener(new ManagedSubscription.ChangeListener() {
            @Override
            public void onDataReceived(List<ManagedDataItem> dataItems, List<DataValue> dataValues) {
                // Обработка полученных данных с датчиков
                for (int i = 0; i < dataItems.size(); i++) {
                    ManagedDataItem dataItem = dataItems.get(i);
                    DataValue dataValue = dataValues.get(i);

                    // Ваши действия с элементом данных и его значением
                    NodeId nodeId = dataItem.getNodeId();
                    Variant value = dataValue.getValue();

                    System.out.println("Node ID: " + nodeId);
                    System.out.println("Value: " + value);
                    System.out.println("------------------------------------");

//                    deviceRepo.save(new DeviceOld(
//                                                    null,
//                                                    nodeId.toParseableString(),
//                                                    "something",
//                                                    value.getValue().toString()
//                                            )
//                    );
                }
            }
        });
    }


    @Scheduled(fixedRate = 5000)
    public void myConnector() throws Exception {
        List<Integer> myList = new ArrayList<>(Arrays.asList(3, 12));

        for (int elem : myList
             ) {
            System.out.println(elem);
            Device device = deviceRepo.findById(elem).get();
            List<String> data = (List<String>) device.getData().get("address");

            executorService.execute(() -> {
                try {
                    readNewDevice(device.getIp(), data, elem);
                } catch (UaException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });

        }
    }

    public void readNewDevice(String ip, List<String> data, int elem) throws UaException, ExecutionException, InterruptedException {

        int namespace = 3;
        String endpointUrl = "opc.tcp://" + ip + ":4840";
        OpcUaClient client = null;
            client = OpcUaClient.create(
                    endpointUrl,
                    endpoints ->
                            endpoints.stream()
                                    .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                    .findFirst(),
                    OpcUaClientConfigBuilder::build
            );
            client.connect().get();

        List<NodeId> nodeIdList = new ArrayList<>();

        data.forEach( x -> nodeIdList.add(new NodeId(namespace, x)));

        List<ReadValueId> nodesToRead = new ArrayList<>();
        nodeIdList.forEach( x -> nodesToRead.add(new ReadValueId(
                x,
                AttributeId.Value.uid(),
                null,
                null
        )));
        saveDataToDatabase(data, client, nodesToRead, elem);

    }

    private void saveDataToDatabase(List<String> data, OpcUaClient client, List<ReadValueId> nodesToRead, int elem) throws InterruptedException, ExecutionException {
        CompletableFuture<ReadResponse> completableFuture = client.read(
                0,
                TimestampsToReturn.Server,
                nodesToRead
        );
        ReadResponse future = completableFuture.get();

        String forDb = "{ ";
        for (int i = 0; i < data.size(); i++) {
            String[] paramNameList = data.get(i).split("\\.");

            String opcParamName = "\"" + paramNameList[paramNameList.length -1].substring(
                            1,
                            paramNameList[paramNameList.length -1].length() - 1);

            if (paramNameList.length > 1) {
                opcParamName += "." + paramNameList[paramNameList.length - 2]
                        .substring(
                                1,
                                paramNameList[paramNameList.length - 2]
                                        .length() - 1);
            }
            opcParamName+= "\"";

            String opcParam = Arrays.stream(future.getResults()).toList().get(i).getValue().getValue().toString();
            if (i == data.size() - 1) {
                forDb+= opcParamName + ": \"" + opcParam + "\"";
                break;
            }
            forDb+= opcParamName + " : \"" + opcParam + "\", ";
        }
        forDb+=" }";

        System.out.println(forDb);

        System.out.printf("Данные за %s загружены. \n", LocalDateTime.now());

        Device device = deviceRepo.getReferenceById(elem);

        System.out.println(device.getName());

        deviceDataRepo.save(new DeviceData(
                null,
                LocalDateTime.now(),
                device,
                forDb
                ));
        client.disconnect();
    }
}
