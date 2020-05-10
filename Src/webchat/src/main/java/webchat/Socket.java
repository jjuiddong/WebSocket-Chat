package webchat;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(value="/chat")
public class Socket {
	private Session session;
	public static Set<Socket> listeners = new CopyOnWriteArraySet<>();
	private static int onlineCount = 0;

	@OnOpen
	public void onOpen(Session session) 
	{
		onlineCount++;
		this.session = session;
		listeners.add(this);
		
		System.out.println("onOpen called, userCount:" + onlineCount);
	}
	
	@OnClose
	public void onClose(Session session)
	{
		onlineCount--;
		listeners.remove(this);
		
		System.out.println("onClose called, userCount:" + onlineCount);
	}
	
	@OnMessage
	public void onMessage(String message)
	{
		System.out.println("onMessage called, message:" + message);
		broadcast(message);
	}
	
	@OnError
	public void onError(Session session, Throwable throwable)
	{
		System.out.println("onClose called, error:" + throwable.getMessage());
		listeners.remove(this);
		onlineCount--;
	}
	
	public static void broadcast(String message)
	{
		for (Socket listener : listeners)
			listener.sendMessage(message);
	}
	
	private void sendMessage(String message)
	{
		try {
			this.session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			System.out.println("Caught exception while sending message to Session " + this.session.getId() + "error:" + e.getMessage());
		}
	}	
	
}
