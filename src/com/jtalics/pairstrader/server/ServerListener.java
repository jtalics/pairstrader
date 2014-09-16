package com.jtalics.pairstrader.server;

import java.util.EventObject;


public interface ServerListener {
	
	public abstract class ServerEvent extends EventObject {
		public ServerEvent(Object source) {
			super(source);
		}
	}
	void onServerEvent(ServerEvent twsEvent);
}
