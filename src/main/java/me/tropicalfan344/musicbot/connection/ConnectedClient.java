package me.tropicalfan344.musicbot.connection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.fan87.facket.Facket;
import me.fan87.facket.api.server.FacketConnection;
import me.tropicalfan344.musicbot.connection.communication.SMain;
import net.dv8tion.jda.api.entities.User;

public class ConnectedClient {

    @Getter
    private final FacketConnection connection;

    @Setter
    @Getter
    private User linkedUser = null;

    @Getter
    private final SMain communicationClass;

    public ConnectedClient(Facket facket, FacketConnection connection) {
        this.connection = connection;
        this.communicationClass = new SMain(facket, connection);
    }

}
