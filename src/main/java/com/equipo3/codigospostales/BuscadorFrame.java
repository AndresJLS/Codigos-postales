package com.equipo3.codigospostales;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

public class BuscadorFrame extends JFrame {

    private final JTextField txtCodigo;
    private final JButton btnBuscar;
    private final JComboBox<CodigoPostal> cmbResultados;


    private final ApiClient apiClient;

    public BuscadorFrame() {
        apiClient = new ApiClient();

        setTitle("Buscador de Códigos Postales - Equipo 3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 200));
        setSize(950, 200);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(15, 15));
        principal.setBorder(new EmptyBorder(25, 30, 25, 30));
        principal.setBackground(Color.WHITE);
        setContentPane(principal);


        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        

        // ---------------- ZONA DE BÚSQUEDA ----------------
        JPanel busqueda = new JPanel(new GridBagLayout());
        busqueda.setOpaque(true);
        busqueda.setBackground(Color.WHITE);
     
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;

        JLabel lblCodigo = new JLabel("Escriba el código postal:");
        lblCodigo.setFont(new Font("SansSerif", Font.BOLD, 15));

        txtCodigo = new JTextField(12);
        txtCodigo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtCodigo.setPreferredSize(new Dimension(160, 35));

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBuscar.setPreferredSize(new Dimension(110, 35));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cmbResultados = new JComboBox<>();
        cmbResultados.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cmbResultados.setPreferredSize(new Dimension(250, 35));
        cmbResultados.setEnabled(false);

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        busqueda.add(lblCodigo, gbc);

        gbc.gridx = 1;
        busqueda.add(txtCodigo, gbc);

        gbc.gridx = 2;
        busqueda.add(btnBuscar, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        busqueda.add(cmbResultados, gbc);

        principal.add(busqueda, BorderLayout.CENTER);

        // ---------------- RESULTADO ----------------
        

        // ---------------- ESTADO ----------------
    

        // Eventos
        btnBuscar.addActionListener(e -> realizarBusqueda());

        txtCodigo.addActionListener(e -> realizarBusqueda());

        cmbResultados.addActionListener(e -> mostrarSeleccion());

        // Para que se vea parecido a la imagen del profesor al abrir.
        txtCodigo.requestFocusInWindow();
    }

    private void realizarBusqueda() {
        String codigo = txtCodigo.getText().trim();

        if (!codigo.matches("\\d{5}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Escriba un código postal válido de 5 dígitos.\nEjemplo: 83100",
                    "Código postal inválido",
                    JOptionPane.WARNING_MESSAGE
            );
            txtCodigo.requestFocusInWindow();
            return;
        }

        btnBuscar.setEnabled(false);
        cmbResultados.setEnabled(false);
       

        SwingWorker<List<CodigoPostal>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CodigoPostal> doInBackground() throws Exception {
                return apiClient.buscar(codigo);
            }

            @Override
            protected void done() {
                try {
                    List<CodigoPostal> resultados = get();

                    if (resultados.isEmpty()) {
                        cmbResultados.setModel(new DefaultComboBoxModel<>());
                        cmbResultados.setEnabled(false);


                        return;
                    }

                    DefaultComboBoxModel<CodigoPostal> modelo =
                            new DefaultComboBoxModel<>();

                    for (CodigoPostal resultado : resultados) {
                        modelo.addElement(resultado);
                    }

                    cmbResultados.setModel(modelo);
                    cmbResultados.setEnabled(resultados.size() > 1);

                    mostrarResultado(resultados.get(0));



                } catch (Exception ex) {
                    String mensaje = ex.getCause() != null
                            ? ex.getCause().getMessage()
                            : ex.getMessage();

                    if (mensaje == null || mensaje.isBlank()) {
                        mensaje = "No fue posible consultar la API.";
                    }

//                   

                   

                    JOptionPane.showMessageDialog(
                            BuscadorFrame.this,
                            mensaje,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    btnBuscar.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void mostrarSeleccion() {
        Object seleccionado = cmbResultados.getSelectedItem();

        if (seleccionado instanceof CodigoPostal codigoPostal) {
            mostrarResultado(codigoPostal);
        }
    }

    private void mostrarResultado(CodigoPostal cp) {

    StringBuilder texto = new StringBuilder();

    texto.append("========================================\n");
    texto.append("       RESULTADO DEL CÓDIGO POSTAL\n");
    texto.append("========================================\n\n");

    texto.append("Código postal   : ")
            .append(valor(cp.codigo()))
            .append("\n");

    texto.append("Asentamiento    : ")
            .append(valor(cp.asentamiento()))
            .append("\n");


}

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? "No disponible" : texto;
    }
}
