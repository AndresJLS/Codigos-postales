package com.equipo3.codigospostales;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BuscadorFrame ventana = new BuscadorFrame();
            ventana.setVisible(true);
        });
    }
}
