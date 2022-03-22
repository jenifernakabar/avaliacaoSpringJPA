package br.com.db1.avaliacao.modelo;



public class ApiMessage {
	
	private String message;
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ApiMessage(String message, int code) {
		this.message = message;
		this.code = code;
	}

}
