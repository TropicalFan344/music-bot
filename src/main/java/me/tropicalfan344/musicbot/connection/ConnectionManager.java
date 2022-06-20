package me.tropicalfan344.musicbot.connection;

import lombok.Getter;
import lombok.SneakyThrows;
import me.fan87.facket.Facket;
import me.fan87.facket.api.FacketServer;
import me.fan87.facket.api.server.FacketConnection;
import me.tropicalfan344.musicbot.MusicBot;
import me.tropicalfan344.musicbot.connection.communication.CPacketsImpl;
import net.dv8tion.jda.api.entities.User;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionManager {

    private final MusicBot musicBot;

    @Getter
    private final FacketServer facket;

    @Getter
    private final Map<FacketConnection, ConnectedClient> clientMap = new HashMap<>();

    private final Map<User, String> linkKeys = new HashMap<>();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(MusicBotConstants.maxConnection);


    @SneakyThrows
    public ConnectionManager(MusicBot musicBot) {
        this.musicBot = musicBot;
        CPacketsImpl.bot = musicBot;

        this.facket = new FacketServer(new InetSocketAddress("0.0.0.0", musicBot.getConfigManager().getConfig().communicationPort), MusicBotConstants.maxConnection, MusicBotConstants.bufferSize, MusicBotConstants.protocolVersion, 0);
        this.facket.setConnectionHandler(new Facket.ConnectionHandler() {
            @Override
            public void onClose(Facket facket, FacketConnection connection) {
                clientMap.remove(connection);
                System.out.println("[-] [RPC] Client has disconnected: " + connection.getSocket().getInetAddress());
            }

            @Override
            public void onConnect(Facket facket, FacketConnection connection) {
                ConnectedClient client = new ConnectedClient(facket, connection);
                clientMap.put(connection, client);
                threadPool.submit(() -> {
                    while (true) {
                        try {
                            client.getCommunicationClass().heartbeat();
                        } catch (Exception ignored) {
                            System.out.println("[!] [RPC] Client has timed out : " + connection.getSocket().getInetAddress());
                            connection.close();
                        }
                        Thread.sleep(1000);
                    }
                });
                System.out.println("[+] [RPC] Client has connected: " + connection.getSocket().getInetAddress());
            }

            @Override
            public Facket.RawData onSendRawData(Facket facket, FacketConnection connection, Facket.RawData data) {
                return Facket.ConnectionHandler.super.onSendRawData(facket, connection, data);
            }
        });
        MusicBotConnection.init(facket);
        this.facket.start();
    }

    public String getKeyOf(User user) {
        String key = linkKeys.get(user);
        if (key == null) {
            key = UUID.randomUUID().toString();
            linkKeys.put(user, key);
        }
        return key;
    }

    public User link(FacketConnection connection, String key) {
        for (User user : linkKeys.keySet()) {
            String expectedKey = linkKeys.get(user);
            if (key.equals(expectedKey)) {
                ConnectedClient connectedClient = getClientMap().get(connection);
                connectedClient.setLinkedUser(user);
                return user;
            }
        }
        return null;
    }

}
