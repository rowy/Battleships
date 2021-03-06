package com.jostrobin.battleships.server.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jostrobin.battleships.common.data.AttackResult;
import com.jostrobin.battleships.common.data.Cell;
import com.jostrobin.battleships.common.data.DefaultCell;
import com.jostrobin.battleships.common.data.GameState;
import com.jostrobin.battleships.common.data.Orientation;
import com.jostrobin.battleships.common.data.Player;
import com.jostrobin.battleships.common.data.Ship;
import com.jostrobin.battleships.common.data.enums.GameUpdate;
import com.jostrobin.battleships.common.network.Command;
import com.jostrobin.battleships.common.network.NetworkHandler;
import com.jostrobin.battleships.common.network.NetworkListener;
import com.jostrobin.battleships.server.ServerManager;
import com.jostrobin.battleships.server.game.Game;
import com.jostrobin.battleships.server.network.Writer;

/**
 * Represents a client.  Extends the player data with server side used objects.
 *
 * @author joscht
 */
public class Client extends Player implements NetworkListener
{
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private Socket socket;

    private ServerManager serverManager;

    private Writer clientWriter;

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    /**
     * If this client has started a game.
     */
    private Game game;

    private DefaultCell[][] field;

    private boolean ready;
    private List<Ship> ships;


    public Client(ServerManager serverManager, Writer clientWriter)
    {
        this.serverManager = serverManager;
        this.clientWriter = clientWriter;
    }
    
    public void reset()
    {
    	ready = false;
    	ships = null;
    	field = null;
    	game = null;
    }

    public void init(Socket socket, Long id, String username) throws IOException
    {
        super.setId(id);
        super.setUsername(username);

        this.socket = socket;
        clientWriter.init(new DataOutputStream(socket.getOutputStream()));
    }

    /**
     * Starts a new Thread to handle client input.
     *
     * @throws IOException
     */
    public void startup() throws IOException
    {
        NetworkHandler handler = new NetworkHandler();
        handler.init(new DataInputStream(socket.getInputStream()));
        handler.addNetworkListener(this);

        Thread thread = new Thread(handler);
        thread.start();
    }

    @Override
    public void notify(Command command)
    {
        if (command != null)
        {
            switch (command.getCommand())
            {
                case Command.LOGIN:
                    try
                    {
                        setState(GameState.NEW);
                        clientWriter.acceptPlayer(getId());
                        setUsername(command.getUsername());
                        serverManager.addClient(this);
                    }
                    catch (IOException e)
                    {
                        logger.debug("Client lost before accepting");
                        // nothing else to do
                    }
                    break;
                case Command.CREATE_GAME:
                    setState(GameState.WAITING_FOR_PLAYERS);

                    serverManager.createGame(this, command);
                    break;
                case Command.CANCEL_GAME:
                    if (getState() == GameState.WAITING_FOR_PLAYERS)
                    {
                        setState(GameState.NEW);
                        serverManager.cancelGame(this);
                    }
                    break;
                case Command.JOIN_GAME:
                    serverManager.joinGame(this, command.getGameId());
                    break;

                case Command.CHAT_MESSAGE:
                    if (game != null)
                    {
                        try
                        {
                            game.notifyAboutChatMessage(command.getUsername(), command.getMessage());
                        }
                        catch (IOException e)
                        {
                            // there was an error in communication
                            serverManager.removeClient(this);
                            serverManager.resendPlayerLists();
                        }
                    }
                    break;

                // the player wants to attack someone else
                case Command.ATTACK:
                    logger.debug("Attack at " + command.getX() + ":" + command.getY() + "," + command.getClientId());
                    serverManager.attack(this, command.getClientId(), command.getX(), command.getY());
                    break;
                case Command.SET_SHIPS:
                    LOG.info("Player '{}' has placed his ships", getUsername());
                    placeShips(command.getShips());
                    serverManager.updateGameState(game);
                    serverManager.sendGameChatMessage(game, getUsername() + " is ready.");
                    break;
            }
        }
        else
        {
            LOG.warn("there was an error in communication");
            serverManager.removeClient(this);
            serverManager.resendPlayerLists();
        }
    }

    public void sendAvailablePlayers(List<Client> clients) throws IOException
    {
        // remove its own players, only send opponents
        List<Client> opponents = new ArrayList<Client>(clients.size());
        for (Client client : clients)
        {
            if (!client.getId().equals(getId()))
            {
                opponents.add(client);
            }
        }
        clientWriter.sendAvailablePlayers(opponents);
    }

    /**
     * Tells the client that the game starts, provides the clientId of the player, which can begin.
     *
     * @param clientId
     */
    public void sendStartGame(Long clientId)
    {
        try
        {
            clientWriter.sendStartGame(clientId);
        }
        catch (Exception e)
        {
            serverManager.removeClient(this);
            serverManager.resendPlayerLists();
        }
    }
    
    public void sendCloseGame()
    {
    	try
    	{
    		clientWriter.sendCloseGame();
    	}
        catch (Exception e)
        {
        	// TODO: And now?
        }
    }

    /**
     * Prepare a new game field.
     */
    public void initializeField(int width, int length)
    {
        field = new DefaultCell[width][length];
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < length; y++)
            {
                DefaultCell cell = new DefaultCell();
                cell.setBoardX(x);
                cell.setBoardY(y);
                field[x][y] = cell;
            }
        }
    }

    /**
     * Attack this player. Returns what happened in the attack. Coordinates are zero based.
     *
     * @param x
     * @param y
     * @return
     */
    public AttackResult attack(int x, int y)
    {
        if (x >= 0 && y >= 0 && x < field.length && y < field[0].length)
        {
            return field[x][y].attack(x, y);
        }
        return AttackResult.INVALID;
    }

    /**
     * Returns the ship at the specified position or null if there is none.
     *
     * @param x
     * @param y
     * @return
     */
    public Ship getShipAtPosition(int x, int y)
    {
        if (x >= 0 && y >= 0 && x < field.length && y < field[0].length)
        {
            return field[x][y].getShip();
        }
        return null;
    }

    public void sendChatMessage(String username, String message) throws IOException
    {
        clientWriter.sendChatMessage(username, message);
    }

    /**
     * The result from an attack to transmit to this client.
     *
     * @param x          where the user attacked
     * @param y          where the user attacked
     * @param result
     * @param attacker
     * @param attacked
     * @param gameUpdate can be null
     * @param ship       can be null if the result != SHIP_DESTROYED
     * @throws Exception
     */
    public void sendAttackResult(int x, int y, AttackResult result, Ship ship, Long attacker, Long attacked, GameUpdate gameUpdate, Long nextPlayer) throws Exception
    {
        clientWriter.sendAttackResult(x, y, result, ship, attacker, attacked, gameUpdate, nextPlayer);
    }

    /**
     * Called when the game is full and players can start placing their ships.
     *
     * @throws IOException
     */
    public void prepareGame(Map<Long, String> participants) throws IOException
    {
        clientWriter.sendPrepareGame(game, participants);
    }

    @Override
    public String toString()
    {
        return "Client [username=" + getUsername() + ", id=" + getId()
                + ", state=" + getState() + "]";
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    public void placeShips(List<Ship> ships)
    {
        this.ships = ships;
        for (Ship ship : ships)
        {
            for (int i = 0; i < ship.getSize(); i++)
            {
                if (ship.getOrientation().equals(Orientation.VERTICAL))
                {
                    Cell cell = field[ship.getPositionX()][ship.getPositionY() + i];
                    cell.setShip(ship);
                    ship.addCell(cell);
                }
                else
                {
                    Cell cell = field[ship.getPositionX() + i][ship.getPositionY()];
                    cell.setShip(ship);
                    ship.addCell(cell);
                }
            }
        }
        ready = true;
    }

    public boolean isReady()
    {
        return ready;
    }

    public boolean isDestroyed()
    {
        boolean destroyed = true;
        for (Ship ship : ships)
        {
            destroyed &= ship.isShipDestroyed();
        }
        return destroyed;
    }
}
