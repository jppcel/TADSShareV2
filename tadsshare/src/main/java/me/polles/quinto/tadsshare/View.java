package me.polles.quinto.tadsshare;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import br.univel.jshare.comum.Arquivo;
import br.univel.jshare.comum.Cliente;
import br.univel.jshare.comum.TipoFiltro;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JFormattedTextField;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class View extends JFrame {

	private JPanel contentPane;
	private JTextField tf_pesquisa;
	private JTextField tf_filtro;
	
	private JButton btnIniciarComoServer;
	private JButton btnAcessar;
	private JButton btnPesquisar;
	
	private JComboBox cb_tipoFiltro;
	private final Main main;
	private JButton btnDownload;
	private JButton btnLimparTabela;
	private JFormattedTextField tf_servidor;
	private JPanel panel;
	private JSplitPane panelSplit;
	private JSplitPane panelListAndTable;
	private JScrollPane panelList;
	private JList<String> list;
	private JScrollPane panelTable;
	private JTable table;
	private JScrollPane scrollPane;
	private JTextPane txt_Log;
	private JTextField tf_porta;
	
	private DefaultTableModel defaultModel = new DefaultTableModel();
	private JLabel label;


	/**
	 * Create the frame.
	 */
	public View(Main main) {
		this.main = main;
		
		setTitle("We Are The Server - TADSShare");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 587, 435);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panelHeader = new JPanel();
		contentPane.add(panelHeader, BorderLayout.NORTH);
		GridBagLayout gbl_panelHeader = new GridBagLayout();
		gbl_panelHeader.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panelHeader.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panelHeader.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelHeader.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 1.0};
		panelHeader.setLayout(gbl_panelHeader);
		
		JLabel lblServidor = new JLabel("Servidor:");
		lblServidor.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblServidor = new GridBagConstraints();
		gbc_lblServidor.anchor = GridBagConstraints.EAST;
		gbc_lblServidor.insets = new Insets(0, 0, 5, 5);
		gbc_lblServidor.gridx = 0;
		gbc_lblServidor.gridy = 0;
		panelHeader.add(lblServidor, gbc_lblServidor);
		
		tf_servidor = new JFormattedTextField();
		GridBagConstraints gbc_tf_servidor = new GridBagConstraints();
		gbc_tf_servidor.gridwidth = 2;
		gbc_tf_servidor.insets = new Insets(0, 0, 5, 5);
		gbc_tf_servidor.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_servidor.gridx = 1;
		gbc_tf_servidor.gridy = 0;
		panelHeader.add(tf_servidor, gbc_tf_servidor);
		
		label = new JLabel(":");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.gridx = 3;
		gbc_label.gridy = 0;
		panelHeader.add(label, gbc_label);
		
		tf_porta = new JTextField();
		GridBagConstraints gbc_tf_porta = new GridBagConstraints();
		gbc_tf_porta.insets = new Insets(0, 0, 5, 5);
		gbc_tf_porta.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_porta.gridx = 4;
		gbc_tf_porta.gridy = 0;
		panelHeader.add(tf_porta, gbc_tf_porta);
		tf_porta.setColumns(10);
		
		btnIniciarComoServer = new JButton("Iniciar como Servidor");
		btnIniciarComoServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				serverConnect();
			}
		});
		
		btnAcessar = new JButton("Conectar");
		btnAcessar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				toggleConnect();
			}
		});
		GridBagConstraints gbc_btnAcessar = new GridBagConstraints();
		gbc_btnAcessar.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAcessar.insets = new Insets(0, 0, 5, 5);
		gbc_btnAcessar.gridx = 5;
		gbc_btnAcessar.gridy = 0;
		panelHeader.add(btnAcessar, gbc_btnAcessar);
		GridBagConstraints gbc_btnIniciarComoServer = new GridBagConstraints();
		gbc_btnIniciarComoServer.insets = new Insets(0, 0, 5, 0);
		gbc_btnIniciarComoServer.gridx = 6;
		gbc_btnIniciarComoServer.gridy = 0;
		panelHeader.add(btnIniciarComoServer, gbc_btnIniciarComoServer);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.WHITE);
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.gridwidth = 7;
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 1;
		panelHeader.add(separator_1, gbc_separator_1);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.WHITE);
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 7;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 2;
		panelHeader.add(separator, gbc_separator);
		
		JLabel lblPesquisa = new JLabel("Pesquisa:");
		GridBagConstraints gbc_lblPesquisa = new GridBagConstraints();
		gbc_lblPesquisa.anchor = GridBagConstraints.EAST;
		gbc_lblPesquisa.insets = new Insets(0, 0, 5, 5);
		gbc_lblPesquisa.gridx = 0;
		gbc_lblPesquisa.gridy = 3;
		panelHeader.add(lblPesquisa, gbc_lblPesquisa);
		
		tf_pesquisa = new JTextField();
		tf_pesquisa.setEnabled(false);
		GridBagConstraints gbc_tf_pesquisa = new GridBagConstraints();
		gbc_tf_pesquisa.gridwidth = 5;
		gbc_tf_pesquisa.insets = new Insets(0, 0, 5, 5);
		gbc_tf_pesquisa.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_pesquisa.gridx = 1;
		gbc_tf_pesquisa.gridy = 3;
		panelHeader.add(tf_pesquisa, gbc_tf_pesquisa);
		tf_pesquisa.setColumns(10);
		
		JLabel lblTipoDeFiltro = new JLabel("Tipo de Filtro:");
		GridBagConstraints gbc_lblTipoDeFiltro = new GridBagConstraints();
		gbc_lblTipoDeFiltro.anchor = GridBagConstraints.EAST;
		gbc_lblTipoDeFiltro.insets = new Insets(0, 0, 5, 5);
		gbc_lblTipoDeFiltro.gridx = 0;
		gbc_lblTipoDeFiltro.gridy = 4;
		panelHeader.add(lblTipoDeFiltro, gbc_lblTipoDeFiltro);
		
		btnPesquisar = new JButton("Pesquisar");
		btnPesquisar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				search();
			}
		});
		btnPesquisar.setEnabled(false);
		GridBagConstraints gbc_btnPesquisar = new GridBagConstraints();
		gbc_btnPesquisar.insets = new Insets(0, 0, 5, 0);
		gbc_btnPesquisar.gridheight = 2;
		gbc_btnPesquisar.fill = GridBagConstraints.BOTH;
		gbc_btnPesquisar.gridx = 6;
		gbc_btnPesquisar.gridy = 3;
		panelHeader.add(btnPesquisar, gbc_btnPesquisar);
		
		cb_tipoFiltro = new JComboBox(TipoFiltro.values());
		cb_tipoFiltro.setEnabled(false);
		cb_tipoFiltro.setEditable(true);
		GridBagConstraints gbc_cb_tipoFiltro = new GridBagConstraints();
		gbc_cb_tipoFiltro.insets = new Insets(0, 0, 5, 5);
		gbc_cb_tipoFiltro.fill = GridBagConstraints.HORIZONTAL;
		gbc_cb_tipoFiltro.gridx = 1;
		gbc_cb_tipoFiltro.gridy = 4;
		panelHeader.add(cb_tipoFiltro, gbc_cb_tipoFiltro);
		
		JLabel lblFiltro = new JLabel("Filtro");
		GridBagConstraints gbc_lblFiltro = new GridBagConstraints();
		gbc_lblFiltro.anchor = GridBagConstraints.EAST;
		gbc_lblFiltro.insets = new Insets(0, 0, 5, 5);
		gbc_lblFiltro.gridx = 2;
		gbc_lblFiltro.gridy = 4;
		panelHeader.add(lblFiltro, gbc_lblFiltro);
		
		tf_filtro = new JTextField();
		tf_filtro.setEnabled(false);
		GridBagConstraints gbc_tf_filtro = new GridBagConstraints();
		gbc_tf_filtro.gridwidth = 3;
		gbc_tf_filtro.insets = new Insets(0, 0, 5, 5);
		gbc_tf_filtro.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_filtro.gridx = 3;
		gbc_tf_filtro.gridy = 4;
		panelHeader.add(tf_filtro, gbc_tf_filtro);
		tf_filtro.setColumns(10);
		
		btnLimparTabela = new JButton("Limpar Tabela");
		btnLimparTabela.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearTable();
			}
		});
		btnLimparTabela.setEnabled(false);
		GridBagConstraints gbc_btnLimparTabela = new GridBagConstraints();
		gbc_btnLimparTabela.insets = new Insets(0, 0, 5, 5);
		gbc_btnLimparTabela.gridx = 5;
		gbc_btnLimparTabela.gridy = 5;
		panelHeader.add(btnLimparTabela, gbc_btnLimparTabela);
		
		btnDownload = new JButton("Download");
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				download();
			}
		});
		btnDownload.setEnabled(false);
		GridBagConstraints gbc_btnDownload = new GridBagConstraints();
		gbc_btnDownload.insets = new Insets(0, 0, 5, 0);
		gbc_btnDownload.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDownload.gridx = 6;
		gbc_btnDownload.gridy = 5;
		panelHeader.add(btnDownload, gbc_btnDownload);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{271, 11, 2, 0};
		gbl_panel.rowHeights = new int[]{4, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		panelSplit = new JSplitPane();
		panelSplit.setResizeWeight(0.7);
		panelSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GridBagConstraints gbc_panelSplit = new GridBagConstraints();
		gbc_panelSplit.gridheight = 2;
		gbc_panelSplit.gridwidth = 3;
		gbc_panelSplit.insets = new Insets(0, 0, 5, 5);
		gbc_panelSplit.fill = GridBagConstraints.BOTH;
		gbc_panelSplit.gridx = 0;
		gbc_panelSplit.gridy = 0;
		panel.add(panelSplit, gbc_panelSplit);
		
		panelListAndTable = new JSplitPane();
		panelListAndTable.setResizeWeight(0.3);
		panelSplit.setLeftComponent(panelListAndTable);
		
		panelList = new JScrollPane();
		panelListAndTable.setLeftComponent(panelList);
		
		list = new JList();
		panelList.setViewportView(list);
		
		panelTable = new JScrollPane();
		panelListAndTable.setRightComponent(panelTable);
		
		table = new JTable();
		panelTable.setViewportView(table);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelSplit.setRightComponent(scrollPane);
		
		txt_Log = new JTextPane();
		txt_Log.setBackground(Color.BLACK);
		txt_Log.setForeground(Color.GREEN);
		scrollPane.setViewportView(txt_Log);
	}
	
	protected void download() {
		int row = table.getSelectedRow();
		ArquivoModel model = (ArquivoModel) table.getModel();
		main.downloadArchive((Cliente) model.getValueAt(row, 4), (Arquivo) model.getValueAt(row,5));
	}

	protected void clearTable() {
		table.setModel(defaultModel);
	}

	public void toggleConnect(){
		if(main.getIsConnected() == 0){
			addLog("CLIENTE: Conexão sendo efetuada em " + tf_servidor.getText() + ":" + tf_porta.getText());
			main.connectServer(tf_servidor.getText(), Integer.valueOf(tf_porta.getText()));
			
			tf_servidor.setEnabled(false);
			tf_porta.setEnabled(false);
			tf_pesquisa.setEnabled(true);
			tf_filtro.setEnabled(true);
			
			btnAcessar.setText("Desconectar");
			btnIniciarComoServer.setEnabled(false);	
			btnPesquisar.setEnabled(true);
			btnDownload.setEnabled(true);
			btnLimparTabela.setEnabled(true);
			
			cb_tipoFiltro.setEnabled(true);
		}else{
			addLog("CLIENTE: Desconexão sendo efetuada em " + txt_Log.getText());
			main.disconnectServer();
			tf_servidor.setEnabled(true);
			tf_servidor.setText("");
			tf_porta.setEnabled(true);
			tf_porta.setText("");
			tf_pesquisa.setEnabled(false);
			tf_filtro.setEnabled(false);

			btnAcessar.setText("Conectar");
			btnIniciarComoServer.setEnabled(true);
			btnPesquisar.setEnabled(false);
			btnDownload.setEnabled(false);
			btnLimparTabela.setEnabled(false);
			
			cb_tipoFiltro.setEnabled(false);
			
			clearTable();
		}
	}
	
	public void serverConnect(){
		addLog("CLIENTE: Conexão efetuada em servidor próprio #IAmTheServer");
		
		main.iAmTheServer();
		tf_servidor.setEnabled(false);
		tf_porta.setEnabled(false);
		tf_pesquisa.setEnabled(true);
		tf_filtro.setEnabled(true);
		
		btnAcessar.setText("Desconectar");
		btnIniciarComoServer.setEnabled(false);
		btnPesquisar.setEnabled(true);
		btnDownload.setEnabled(true);
		btnLimparTabela.setEnabled(true);
		
		cb_tipoFiltro.setEnabled(true);
	}
	
	public void addLog(String log){
		txt_Log.setText(txt_Log.getText() + log + "\n");
	}
	
	public void setWindowTitle(String ip){
		this.setTitle("We Are The Server - TADSShare - IP: " + ip);
	}
	
	public void search(){
		Map<Cliente, List<Arquivo>> arquivos = main.search(tf_pesquisa.getText(), (TipoFiltro) cb_tipoFiltro.getSelectedItem(), tf_filtro.getText());
		if(arquivos.size() == 0){
			clearTable();
		}else{
			TableModel arquivoModel = new ArquivoModel(arquivos);
			table.setModel(arquivoModel);
		}
	}
	
	public void popup(String text){
		JOptionPane.showMessageDialog(null, text);
	}

	public void setJList(DefaultListModel<String> listModel) {
		list.setModel(listModel);
	}

}
