package com.jtalics.pairstrader;

import java.util.EventObject;

import com.jtalics.pairstrader.ServerErrorListener.ErrorEvent;

public interface ServerErrorListener {
	public class ErrorEvent extends EventObject {

		public final int id;
		public final int errorCode;
		public final String errorMsg;
		
		public ErrorEvent(Object source, int id, int errorCode, String errorMsg) {
			super(source);
			this.id =id;
			this.errorCode = errorCode;
			this.errorMsg = errorMsg;
		}
	}

	public void onServerErrorEvent(ErrorEvent ee);
}
