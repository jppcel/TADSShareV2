package me.polles.quinto.tadsshare;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import br.univel.jshare.comum.Cliente;
import br.univel.jshare.comum.IServer;
import br.univel.jshare.comum.TipoFiltro;
import br.dagostini.exemplos.Md5Util;
import br.univel.jshare.comum.Arquivo;

public class Main {
	private InetAddress IP;
	private int nextId = 1;
	private View view;
	private IServer iserver;
	private Cliente cliente;
	private int isConnected;
	private Thread server;
	private int port;
	private File share;
	
	public Main(){
		try {
			port = Integer.valueOf(JOptionPane.showInputDialog("Qual é a porta que deseja iniciar o servidor?", "1818"));
			
			
			IP = InetAddress.getLocalHost();
			view = new View(Main.this);
			view.setVisible(true);
			view.setWindowTitle(IP.getHostAddress() + ":" + port);
			

			share = new File("share");
			if(!share.exists()){
				share.mkdir();
			}

			cliente = new Cliente();
			cliente.setNome(IP.getHostName());
			cliente.setPorta(port);
			cliente.setIp(IP.getHostAddress());
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	public List<Arquivo> capturaArquivos(File diretorio){
		List<Arquivo> listaArquivos = new ArrayList<Arquivo>();
		for(File file : diretorio.listFiles()){
			if(file.isFile()){
				Arquivo arquivoComum = new Arquivo();
				arquivoComum.setId(nextId++);
				arquivoComum.setNome(file.getName());
				arquivoComum.setPath(file.getPath());
				arquivoComum.setExtensao(file.getName().substring(file.getName().lastIndexOf("."), file.getName().length()));
				arquivoComum.setDataHoraModificacao(new Date(file.lastModified()));
				arquivoComum.setTamanho(file.length());
				arquivoComum.setMd5(Md5Util.getMD5Checksum(file.getAbsolutePath()));
				listaArquivos.add(arquivoComum);
			}else{
				listaArquivos.addAll(capturaArquivos(file));
			}
		}
		return listaArquivos;
	}
	
	private void rmiConnect(String ip, int port){
		Registry registry;
		try {			
			server = new Thread(new Server(Main.this));
			server.start();
			
			Thread.sleep(500);
			
			registry = LocateRegistry.getRegistry(ip, port);
			iserver =  (IServer) registry.lookup(IServer.NOME_SERVICO);
			publishArchivesList();
		} catch (RemoteException e) {
			view.addLogClient("CLIENTE: Problemas de conexão com o servidor informado.");
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void connectServer(String ip, int port){
		try {
			rmiConnect(ip, port);
			iserver.registrarCliente(cliente);
			isConnected = 1;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void iAmTheServer(){
		try {
			rmiConnect(IP.getHostAddress(), port);
			iserver.registrarCliente(cliente);
			isConnected = 1;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnectServer(){
		try {
			iserver.desconectar(cliente);
			iserver = null;
			isConnected = 0;
			server.interrupt();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void publishArchivesList(){
		try {
			List<Arquivo> mapArquivo = capturaArquivos(new File("share"));
			iserver.publicarListaArquivos(cliente, mapArquivo);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public Map<Cliente, List<Arquivo>> search(String query, TipoFiltro tipoFiltro, String filtro){
		try {
			return iserver.procurarArquivo(query, tipoFiltro, filtro);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void logServer(String log){
		view.addLogServer(log);
	}
	
	public void logClient(String log){
		view.addLogClient(log);
	}

	public int getIsConnected() {
		return isConnected;
	}

	public void downloadArchive(Cliente cliente, Arquivo arquivo) {
		try {
			Registry serverDownload = LocateRegistry.getRegistry(cliente.getIp(), cliente.getPorta());
			IServer iserverDownload = (IServer) serverDownload.lookup(IServer.NOME_SERVICO);
			byte[] data = iserverDownload.baixarArquivo(cliente, arquivo);
			if(data != null){
				String path = "share"+File.separatorChar+arquivo.getNome();
				File file = new File(path);
				
				if(file.exists()){
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					path = "share"+File.separatorChar+ timestamp.getTime() + "_" + arquivo.getNome();
				}
				
				Files.write(Paths.get(path), data, StandardOpenOption.CREATE);
				
				if(arquivo.getMd5().equals(Md5Util.getMD5Checksum(path))){
					view.popup("Arquivo baixado!");
					publishArchivesList();
				}else{
					view.popup("Arquivo corrompido!");
					file = new File(path);
					file.delete();
				}
				iserverDownload = null;
				serverDownload = null;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateClientList(List<Cliente> clientes) {
		DefaultListModel<String> listModel = new DefaultListModel<>();
		clientes.forEach(e -> {
			listModel.addElement(e.getNome());
		});
		view.setJList(listModel);
	}
	public int getPort(){
		return port;
	}

	public void atualizarArquivos() {
		capturaArquivos(share);
	}
}
