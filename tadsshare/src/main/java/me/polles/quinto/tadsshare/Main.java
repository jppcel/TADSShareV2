package me.polles.quinto.tadsshare;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	public Main(){
		try {
			view = new View(Main.this);
			view.setVisible(true);
			
			IP = InetAddress.getLocalHost();
			view.setWindowTitle(IP.getHostAddress());

			File share = new File("share");
			if(!share.exists()){
				share.mkdir();
			}

			Thread server = new Thread(new Server(Main.this));
			server.start();

			cliente = new Cliente();
			cliente.setNome(IP.getHostName());
			cliente.setPorta(Server.PORTA_TCPIP);
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
			registry = LocateRegistry.getRegistry(ip, port);
			iserver =  (IServer) registry.lookup(IServer.NOME_SERVICO);
			publishArchivesList();
		} catch (RemoteException e) {
			view.addLog("Problemas de conexão");
		} catch (NotBoundException e) {
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
			rmiConnect(IP.getHostAddress(), Server.PORTA_TCPIP);
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
	
	public void log(String log){
		view.addLog(log);
	}

	public int getIsConnected() {
		return isConnected;
	}
}
