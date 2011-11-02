package com.jostrobin.battleships;

import com.jostrobin.battleships.service.network.rmi.chat.server.ChatServer;
import com.jostrobin.battleships.ui.controller.GameSelectionController;
import com.jostrobin.battleships.ui.controller.RegistrationController;
import com.jostrobin.battleships.ui.frames.GameFrame;
import com.jostrobin.battleships.ui.frames.GameSelectionFrame;

/**
 * This is the entry point of the application.
 *
 * @author rowyss
 *         Date: 18.10.11 Time: 22:33
 */
public class ApplicationController
{

    public static void main(String... args)
    {

        // thread rmi

        // thread broadcast

        // gui


        new RegistrationController().showRegistrationDialog();

        new GameFrame();
        new ChatServer();

        GameSelectionFrame f = new GameSelectionFrame(new GameSelectionController());
        f.setVisible(true);
    }

}
