package com.jostrobin.battleships.view.panels;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jostrobin.battleships.common.PlacementHelper;
import com.jostrobin.battleships.common.data.Cell;
import com.jostrobin.battleships.common.data.Ship;
import com.jostrobin.battleships.common.network.Command;
import com.jostrobin.battleships.model.ShipsModel;
import com.jostrobin.battleships.view.listeners.AttackListener;
import com.jostrobin.battleships.view.listeners.SelectionListener;

@SuppressWarnings("serial")
public class GamePanel extends JPanel
{
    private Logger logger = LoggerFactory.getLogger(GamePanel.class);

    /**
     * Map holding a battlefieldpanel for each of the opponents. clientId as key, panel as value.
     */
    private Map<Long, BattleFieldPanel> battlefieldPanels;

    private List<AttackListener> attackListeners = new ArrayList<AttackListener>();

    private ShipsModel shipsModel;
    private PlacementHelper placementHelper;

    /**
     * Initializes the UI for the specified game type.
     *
     * @param length    field length
     * @param width     field width
     * @param opponents list of opponents
     */
    public void initUi(int length, int width, Map<Long, String> opponents)
    {
    	this.removeAll();
        setLayout(new FlowLayout());

        battlefieldPanels = new HashMap<Long, BattleFieldPanel>();
        for (Map.Entry<Long, String> entry : opponents.entrySet())
        {
            final Long id = entry.getKey();
            BattleFieldPanel panel = new BattleFieldPanel(entry.getValue());
            panel.initializeFieldSize(length, width);
            battlefieldPanels.put(id, panel);

            panel.addSelectionListener(new SelectionListener<Cell>()
            {
                @Override
                public void selected(Cell cell)
                {
                    cellClicked(cell, id);
                }
            });

            this.add(panel);
        }
    }

    public void reset()
    {
        battlefieldPanels.clear();
        if (shipsModel != null)
        {
        	shipsModel.setCells(new ArrayList<Cell>());
        }
        this.removeAll();
    }

    public void initializeFieldSize(int length, int width)
    {
        for (Long clientId : battlefieldPanels.keySet())
        {
            BattleFieldPanel panel = battlefieldPanels.get(clientId);
            panel.initializeFieldSize(length, width);
        }
    }

    public void addAttackListener(AttackListener listener)
    {
        attackListeners.add(listener);
    }

    /**
     * The specified cell on the field of the specified player has been clicked. Called by listeners.
     *
     * @param cell
     * @param clientId
     */
    private void cellClicked(Cell cell, Long clientId)
    {
        logger.debug("Cell {} of client {} clicked", cell, clientId);
        for (AttackListener listener : attackListeners)
        {
            if (!cell.isHit())
            {
                listener.attack(cell.getBoardX(), cell.getBoardY(), clientId);
            }
        }
    }

    public void hitCell(Command command)
    {
        BattleFieldPanel panel = battlefieldPanels.get(command.getAttackedClient());
        panel.hitCell(command.getX(), command.getY(), command.getAttackResult());
    }

    public void addShip(Long attackedClientId, Ship ship)
    {
        BattleFieldPanel panel = battlefieldPanels.get(attackedClientId);
        PlacementHelper helper = new PlacementHelper(panel);
        helper.placeShipWithoutCheck(ship, ship.getPositionX(), ship.getPositionY());
        ship.setSelected(false);
    }

    public void changeCurrentPlayer(Long playerId)
    {
        for (Map.Entry<Long, BattleFieldPanel> entry : battlefieldPanels.entrySet())
        {
            BattleFieldPanel battleFieldPanel = entry.getValue();
            Long currentId = entry.getKey();
            battleFieldPanel.setCurrent(playerId.equals(currentId));
        }
    }

}
