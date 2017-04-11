package me.polles.quinto.tadsshare;

import javax.swing.event.TableModelListener;
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

	private Object[][] matrix;
	private int rows;
	private static final long serialVersionUID = -5259217698492435038L;
	
	public ArquivoModel(Map<Cliente, List<Arquivo>> mapArquivos){
		if(!mapArquivos.isEmpty()){
			rows = 0;
			mapArquivos.entrySet().forEach(e -> {
				rows+=e.getValue().size();
			});
			
			matrix = new Object[rows][6];
			
			
			
			int row = 0;
			
			for (Entry<Cliente, List<Arquivo>> e : mapArquivos.entrySet()) {
				for (Arquivo arq : e.getValue()) {
					System.out.println(e.getKey().getNome());
					matrix[row][0] = e.getKey().getNome();
					matrix[row][1] = e.getKey().getIp();
					matrix[row][2] = arq.getNome();
					matrix[row][3] = arq.getTamanho();
					matrix[row][4] = arq;
					matrix[row][5] = e;
					row++;
				}
			}
		}
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnCount() {
		return 4;
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
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return matrix.length;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return matrix[arg0][arg1];
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

}
