package me.polles.quinto.tadsshare;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.univel.jshare.comum.Arquivo;
import br.univel.jshare.comum.Cliente;

public class ArquivoModel extends AbstractTableModel implements TableModel{

	private Map<Object, List<Object>> matrix2;
	private int rows;
	private static final long serialVersionUID = -5259217698492435038L;
	
	public ArquivoModel(Map<Cliente, List<Arquivo>> mapArquivos){
		if(!mapArquivos.isEmpty()){			
			
			rows = 0;
			matrix2 = new HashMap<Object, List<Object>>();
			List<Object> objetos = new ArrayList<Object>();
			for (Entry<Cliente, List<Arquivo>> e : mapArquivos.entrySet()) {
				for (Arquivo arquivo : e.getValue()) {
					objetos = new ArrayList<Object>();
					objetos.add(e.getKey().getNome());
					objetos.add(e.getKey().getIp() + ":" + e.getKey().getPorta());
					objetos.add(arquivo.getNome());
					objetos.add(String.valueOf(arquivo.getTamanho()));
					objetos.add(arquivo.getMd5());
					objetos.add(e.getKey());
					objetos.add(arquivo);
					matrix2.put(rows++, objetos);
				}
			}
		}
	}


	@Override		
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(int arg0) {
		switch(arg0){
			case 0:
				return "Nome";
			case 1:
				return "IP";
			case 2:
				return "Nome do Arquivo";
			case 3:
				return "Tamanho";
			case 4:
				return "Hash";
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return rows;
	}
	
	public void setRowCount(int rows) {
		this.rows = rows;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return matrix2.get(arg0).get(arg1);
	}

}
