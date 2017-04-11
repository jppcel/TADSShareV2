package me.polles.quinto.tadsshare;

import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import br.univel.jshare.comum.Cliente;

public class ClienteModel implements ListModel<Cliente>{
	
	private List<Cliente> clientes;
	
	public ClienteModel(List<Cliente> clientes){
		this.clientes = clientes;
	}

	@Override
	public Cliente getElementAt(int arg0) {
		return clientes.get(arg0);
	}

	@Override
	public int getSize() {
		return clientes.size();
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

}
