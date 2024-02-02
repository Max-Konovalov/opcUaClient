package ru.brighttech.opcuaclient.domain.service;

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
import org.springframework.stereotype.Component;
import ru.brighttech.opcuaclient.domain.persistence.entity.DeviceOld;
import ru.brighttech.opcuaclient.domain.persistence.repository.DeviceRepo;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class OpcUaConnetor {

    private DeviceRepo deviceRepo;

    public OpcUaConnetor(DeviceRepo deviceRepo) {
        this.deviceRepo = deviceRepo;
    }

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

                    deviceRepo.save(new DeviceOld(
                                                    null,
                                                    nodeId.toParseableString(),
                                                    "something",
                                                    value.getValue().toString()
                                            )
                    );
                }
            }
        });
    }
}
