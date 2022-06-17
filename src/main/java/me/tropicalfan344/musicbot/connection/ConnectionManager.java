package me.tropicalfan344.musicbot.connection;

import lombok.Getter;
import lombok.SneakyThrows;
import me.fan87.facket.Facket;
import me.fan87.facket.api.CommunicationClass;
import me.fan87.facket.api.FacketServer;
import me.fan87.facket.api.server.FacketConnection;
import me.tropicalfan344.musicbot.MusicBot;
import me.tropicalfan344.musicbot.connection.communication.CMain;
import me.tropicalfan344.musicbot.connection.communication.CMainImpl;
import net.dv8tion.jda.api.entities.User;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConnectionManager {

    private final MusicBot musicBot;

    @Getter
    private final FacketServer facket;

    @Getter
    private final Map<FacketConnection, ConnectedClient> clientMap = new HashMap<>();

    private final Map<User, String> linkKeys = new HashMap<>();


    @SneakyThrows
    public ConnectionManager(MusicBot musicBot) {
        this.musicBot = musicBot;
        CMainImpl.bot = musicBot;

        this.facket = new FacketServer(new InetSocketAddress("0.0.0.0", musicBot.getConfigManager().getConfig().communicationPort), 100, MusicBotConstants.bufferSize, MusicBotConstants.protocolVersion, 0);
        this.facket.setConnectionHandler(new Facket.ConnectionHandler() {
            @Override
            public void onClose(Facket facket, FacketConnection connection) {
                clientMap.remove(connection);
                System.out.println("[RPC] Client has disconnected: " + connection.getSocket().getInetAddress());
            }

            @Override
            public void onConnect(Facket facket, FacketConnection connection) {
                clientMap.put(connection, new ConnectedClient(facket, connection));
                System.out.println("[RPC] Client has connected: " + connection.getSocket().getInetAddress());
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
