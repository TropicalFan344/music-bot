import club.bottomservices.discordrpc.lib.DiscordRPCClient;
import lombok.SneakyThrows;
import me.fan87.facket.Facket;
import me.fan87.facket.api.FacketClient;
import me.fan87.facket.api.server.FacketConnection;
import me.tropicalfan344.musicbot.connection.MusicBotConnection;
import me.tropicalfan344.musicbot.connection.MusicBotConstants;
import me.tropicalfan344.musicbot.connection.communication.CPackets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Main {
    public static DiscordRPCClient client = new DiscordRPCClient("987450736470278225");

    public static FacketClient facket = null;
    public static CPackets cMain = null;

    @SneakyThrows
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Requires host and port(optional)");
        }
        int port = MusicBotConstants.defaultPort;

        if (args.length == 2) {
            port = Integer.parseInt(args[1]);
        }

        System.out.print("Please enter the link key\n>> ");
        Scanner scanner = new Scanner(System.in);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(args[0], port);

        String key = scanner.nextLine();
        connect(inetSocketAddress, key);
        new Thread() {
            @Override
            @SneakyThrows
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        cMain.heartbeat();
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Reconnecting...");
                        connect(inetSocketAddress, key);
                    }
                }
            }
        }.start();

        client.connect();
    }

    @SneakyThrows
    public static void connect(InetSocketAddress inetSocketAddress, String key) {
        facket = new FacketClient(inetSocketAddress, MusicBotConstants.bufferSize, MusicBotConstants.protocolVersion, 0);
        MusicBotConnection.init(facket);
        facket.setConnectionHandler(new Facket.ConnectionHandler() {
            @Override
            @SneakyThrows
            public Facket.RawData onReceiveRawData(Facket facket, FacketConnection connection, Facket.RawData data) {
                return Facket.ConnectionHandler.super.onReceiveRawData(facket, connection, data);
            }
        });
        facket.start();
        cMain = new CPackets(facket, facket.getConnection());
        String response = cMain.link(key);
        if (response == null) {
            System.err.println("Invalid Key!");
            System.exit(-1);
        }
        System.out.println("Connection Established: " + response);
    }

}
