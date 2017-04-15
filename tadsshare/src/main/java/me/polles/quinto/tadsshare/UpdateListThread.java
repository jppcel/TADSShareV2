package me.polles.quinto.tadsshare;

public class UpdateListThread implements Runnable{
	private Main main;
	
	public UpdateListThread(Main main){
		this.main = main;
	}
	
	@Override
	public void run() {
		try {
			while(true){
				main.logClient("CLIENTE: Atualizando lista de arquivos...");
				main.atualizarArquivos();
				main.publishArchivesList();
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
