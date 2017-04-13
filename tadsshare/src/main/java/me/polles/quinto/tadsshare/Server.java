package me.polles.quinto.tadsshare;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.univel.jshare.comum.Arquivo;
import br.univel.jshare.comum.Cliente;
import br.univel.jshare.comum.IServer;
import br.univel.jshare.comum.TipoFiltro;

/**
 * @author Joao
 *
 */
public class Server implements IServer, Runnable, Serializable {
	private static final long serialVersionUID = 1L;
	private List<Cliente> clientes;
	private int proxId;
	private Map<Cliente, List<Arquivo>> arquivos;
	private IServer iserver;
	private final Main main;
	
	public static final int PORTA_TCPIP = 1818;
	
	public Server(Main main){
		clientes = new ArrayList<Cliente>();
		arquivos = new HashMap<Cliente, List<Arquivo>>();
		this.main = main;
	}

	public void registrarCliente(Cliente cliente) throws RemoteException {
		//cliente.setId(proxId);
		proxId++;
		clientes.add(cliente);
		
		main.log("SERVIDOR: Conexão -> " + cliente.getNome());
		
		main.updateClientList(clientes);
	}

	public void publicarListaArquivos(Cliente cliente, List<Arquivo> lista) throws RemoteException {
		if(lista.size() > 0){
			if(arquivos.get(cliente) != null){
				arquivos.remove(cliente);
			}
			arquivos.put(cliente, lista);
			main.log("SERVIDOR: Lista de Arquivos -> O cliente " + cliente.getNome() + " publicou uma lista de arquivos.");
		}
	}

	public Map<Cliente, List<Arquivo>> procurarArquivo(String query, TipoFiltro tipoFiltro, String filtro) throws RemoteException {
		
		Map<Cliente, List<Arquivo>> resultado = new HashMap<Cliente, List<Arquivo>>();
		
		
		arquivos.forEach((cliente, listaArquivos) ->{
			List<Arquivo> resultadoArquivos = new ArrayList<Arquivo>();
			listaArquivos.forEach(arquivo -> {
				
				if(query.isEmpty() && filtro.isEmpty()){
					resultadoArquivos.add(arquivo);
				} else if(arquivo.getNome().contains(query) || query.isEmpty()){
					if(filtro.isEmpty()){
						resultadoArquivos.add(arquivo);
					} else if (tipoFiltro == TipoFiltro.EXTENSAO) {
						if(arquivo.getExtensao().contains(filtro)){
							resultadoArquivos.add(arquivo);
						}
					} else if (tipoFiltro == TipoFiltro.TAMANHO_MIN){
						if(arquivo.getTamanho() >= Float.valueOf(filtro)){
							resultadoArquivos.add(arquivo);
						}
					} else if (tipoFiltro == TipoFiltro.TAMANHO_MAX) {
						if(arquivo.getTamanho() <= Float.valueOf(filtro)){
							resultadoArquivos.add(arquivo);
						}
					}
				}
				
			});
			
			if(!resultadoArquivos.isEmpty()){
				resultado.put(cliente, resultadoArquivos);
			}
		});
		
		return resultado;
	}

	public byte[] baixarArquivo(Cliente cliente, Arquivo arquivo) throws RemoteException {
		byte[] data = null;
		Path path = Paths.get(arquivo.getPath());
		try {
			data = Files.readAllBytes(path);
			main.log("CLIENTE: " + cliente.getNome() + ", " + cliente.getIp() + " baixou o arquivo " + arquivo.getNome());
			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void desconectar(Cliente cliente) throws RemoteException {
		if(arquivos.get(cliente) != null){
			arquivos.remove(cliente);
		}
		clientes.remove(cliente);
		main.log("SERVIDOR: Desconexão -> " + cliente.getNome() + "(" +  cliente.getIp() + ")");
	}

	@Override
	public void run() {
		try {
			main.log("SERVIDOR: Iniciando...");
			iserver = (IServer) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.createRegistry(PORTA_TCPIP);
			registry.rebind(IServer.NOME_SERVICO, iserver);
			main.log("SERVIDOR: Iniciado. Aguardando conexões.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
