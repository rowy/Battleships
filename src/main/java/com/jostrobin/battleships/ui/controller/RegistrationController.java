package com.jostrobin.battleships.ui.controller;

import java.util.UUID;

import com.jostrobin.battleships.data.Configuration;
import com.jostrobin.battleships.service.network.rmi.RmiManager;
import com.jostrobin.battleships.session.ApplicationState;
import com.jostrobin.battleships.ui.frames.GameSelectionFrame;
import com.jostrobin.battleships.ui.frames.RegistrationDialog;

/**
 * @author rowyss
 *         Date: 19.10.11 Time: 17:02
 */
public class RegistrationController
{
    RegistrationDialog dialog;

    public void showRegistrationDialog()
    {
        dialog = new RegistrationDialog(this);
    }

    public void registerUser(String username)
    {
        if (username == null || username.trim().isEmpty())
        {
            dialog.showMessage("You must provide a name!");
        }
        else
        {
            if (dialog != null && dialog.isActive())
            {
                dialog.dispose();
            }

            String id = username + UUID.randomUUID().getLeastSignificantBits();
            Configuration config = Configuration.getInstance();
            config.setId(id);

            ApplicationState state = ApplicationState.getInstance();
            state.setUsername(username);

            // prepare rmi things
            RmiManager rmiManager = RmiManager.getInstance();
            rmiManager.startupRmiServices();

            // show the next frame
            GameSelectionController gameSelectionController = new GameSelectionController();
            GameSelectionFrame f = new GameSelectionFrame(gameSelectionController);
            gameSelectionController.setGameSelectionFrame(f);
            f.setVisible(true);

            // try to find games
            gameSelectionController.refresh(true);
        }
    }

}
