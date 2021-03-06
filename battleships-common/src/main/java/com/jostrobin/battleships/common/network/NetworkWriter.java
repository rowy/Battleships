package com.jostrobin.battleships.common.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import com.jostrobin.battleships.common.data.GameData;
import com.jostrobin.battleships.common.data.Player;
import com.jostrobin.battleships.common.data.Ship;

public class NetworkWriter
{
    private DataOutputStream outputStream;

    public NetworkWriter(DataOutputStream outputStream) throws IOException
    {
        this.outputStream = outputStream;
    }

    public void login(String username) throws IOException
    {
        outputStream.writeInt(Command.LOGIN);
        outputStream.writeUTF(username);
    }

    public void joinGame(Player player) throws IOException
    {
        outputStream.writeInt(Command.JOIN_GAME);
        outputStream.writeLong(player.getGameData().getId());
    }

    public void createGame(GameData game) throws IOException
    {
        outputStream.writeInt(Command.CREATE_GAME);
        outputStream.writeUTF(game.getMode().name());
        outputStream.writeInt(game.getMaxPlayers());
        outputStream.writeInt(game.getFieldLength());
        outputStream.writeInt(game.getFieldWidth());
        outputStream.writeInt(game.getNrOfAircraftCarriers());
        outputStream.writeInt(game.getNrOfBattleships());
        outputStream.writeInt(game.getNrOfDestroyers());
        outputStream.writeInt(game.getNrOfSubmarines());
        outputStream.writeInt(game.getNrOfPatrolBoats());
    }

    public void sendChatMessage(String username, String message) throws IOException
    {
        outputStream.writeInt(Command.CHAT_MESSAGE);
        outputStream.writeUTF(username);
        outputStream.writeUTF(message);
    }

    public void sendShipPlaced(List<Ship> ships) throws IOException
    {
        outputStream.writeInt(Command.SET_SHIPS);
        outputStream.writeInt(ships.size());
        for (Ship ship : ships)
        {
            outputStream.writeInt(ship.getPositionX());
            outputStream.writeInt(ship.getPositionY());
            outputStream.writeInt(ship.getSize());
            outputStream.writeUTF(ship.getOrientation().name());
            outputStream.writeUTF(ship.getType().name());
        }
    }

    public void sendAttack(int x, int y, Long clientId) throws IOException
    {
        outputStream.writeInt(Command.ATTACK);
        outputStream.writeInt(x);
        outputStream.writeInt(y);
        outputStream.writeLong(clientId);
    }

    public void sendCancelGame() throws IOException
    {
        outputStream.writeInt(Command.CANCEL_GAME);
    }
}
