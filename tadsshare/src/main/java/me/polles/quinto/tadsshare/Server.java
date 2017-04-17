package me.polles.quinto.tadsshare;

import java.io.File;
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
import me.polles.quinto.tadsshare.exceptions.FileNotFoundException;

/**
 * @author Joao
 *
 */
public class Server implements IServer, Runnable, Serializable {
	private static final long serialVersionUID = 1L;
	private List<Cliente> clientes;
	private Map<Cliente, List<Arquivo>> arquivos;
	private IServer iserver;
	private final Main main;
	
	public Server(Main main){
		clientes = new ArrayList<Cliente>();
		arquivos = new HashMap<Cliente, List<Arquivo>>();
		this.main = main;
	}

	public void registrarCliente(Cliente cliente) throws RemoteException {
		clientes.add(cliente);
		
		main.logServer("SERVIDOR: Conexão -> " + cliente.getNome());
		
		main.updateClientList(clientes);
	}

	public void publicarListaArquivos(Cliente cliente, List<Arquivo> lista) throws RemoteException {
		if(lista.size() > 0){
			if(arquivos.get(cliente) != null){
				arquivos.remove(cliente);
			}
			if(lista.isEmpty()){
				main.logServer("SERVIDOR: Lista de Arquivos -> O cliente " + cliente.getNome() + " tentou publicar uma lista de arquivos vazia.");
			}else{
				arquivos.put(cliente, lista);
				main.logServer("SERVIDOR: Lista de Arquivos -> O cliente " + cliente.getNome() + " publicou uma lista de arquivos.");
			}
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
		File file = new File(arquivo.getPath());
		try {
			if(file.exists()){
				data = Files.readAllBytes(path);
				main.logClient("CLIENTE: " + cliente.getNome() + ", " + cliente.getIp() + " baixou o arquivo " + arquivo.getNome());
				main.addUp(arquivo.getTamanho());
				return data;
			}else{
				main.atualizarArquivos();
				throw new FileNotFoundException();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e){
			main.logServer("SERVIDOR: O cliente " + cliente.getNome() + "(" + cliente.getIp() + ") tentou baixar o arquivo " + arquivo.getNome() + " que não se encontra mais disponível.");
		}
		return null;
	}

	public void desconectar(Cliente cliente) throws RemoteException {
		if(arquivos.get(cliente) != null){
			arquivos.remove(cliente);
		}
		clientes.remove(cliente);
		main.updateClientList(clientes);
		main.logServer("SERVIDOR: Desconexão -> " + cliente.getNome() + "(" +  cliente.getIp() + ")");
	}

	@Override
	public void run() {
		try {
			main.logServer("SERVIDOR: Iniciando...");
			iserver = (IServer) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.createRegistry(main.getPort());
			registry.rebind(IServer.NOME_SERVICO, iserver);
			main.logServer("SERVIDOR: Iniciado. Aguardando conexões.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
