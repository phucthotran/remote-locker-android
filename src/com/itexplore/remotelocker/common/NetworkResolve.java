package com.itexplore.remotelocker.common;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import android.content.Context;

import com.itexplore.remotelocker.R;
import com.itexplore.remotelocker.communication.Command;
import com.itexplore.remotelocker.communication.ICommandSender;
import com.itexplore.remotelocker.communication.ICommunicationProvider;
import com.itexplore.remotelocker.communication.InvokeCommand;
import com.itexplore.remotelocker.communication.StringCommandSender;
import com.itexplore.remotelocker.communication.TcpCommunication;

public class NetworkResolve {
    
    private Context mContext;
    private ICommunicationProvider mProvider;
    private ICommandSender mSender;
    private String mHostName;
    
    public NetworkResolve(Context context) {
        this.mContext = context;
    }

    public String[] getLocalAddress() throws UnknownHostException, IOException {
        String currentAddress = null;
        
        //GET CURRENT DEVICE'S IP ADDRESS        
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        
        while(netInterfaces.hasMoreElements()) {
            NetworkInterface current = netInterfaces.nextElement();
            
            if(!current.isUp() || current.isLoopback() || current.isVirtual())
                continue;
            
            Enumeration<InetAddress> addresses = current.getInetAddresses();
            
            while(addresses.hasMoreElements()) {
                InetAddress currentAddr = addresses.nextElement();
                
                if(currentAddr.isLoopbackAddress())
                    continue;
                
                //Get ip v4 only
                if(currentAddr instanceof Inet4Address)
                    currentAddress = currentAddr.getHostAddress();
            }
        }
        
        InetAddress localhost = InetAddress.getByName(currentAddress);
        byte[] ip = localhost.getAddress();
        List<String> localAddress = new Vector<String>();
        
        int startSubnetIdx = 1;
        int endSubnetIdx = 254;
        InetAddress tmpAddress;
        
        while(startSubnetIdx <= endSubnetIdx) {
            ip[3] = (byte)startSubnetIdx;
            
            tmpAddress = InetAddress.getByAddress(ip);
            
            if(tmpAddress.isReachable(50)) {
                localAddress.add(tmpAddress.getHostAddress());
            }
            
            startSubnetIdx++;
        }
        
        String [] address = new String[localAddress.size()];
        localAddress.toArray(address);
        
        return address;
    }
    
    public synchronized boolean serviceAvailable(String address) {
        if(address == null)
            return false;
        
        mProvider = new TcpCommunication(address, Integer.parseInt(mContext.getString(R.string.remotelocker_server_port)));
        mProvider.setCommunicationObject(this);
        
        mSender = new StringCommandSender();
        mSender.setProvider(mProvider);
        
        try {
            mProvider.start();
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        
        if(!((TcpCommunication)mProvider).isConnected()) {
            return false;
        }
        
        mSender.send(Command.COMPUTER_NAME, null);

        return true;
    }
    
    @InvokeCommand(value = Command.COMPUTER_NAME_OUTPUT, hasInput = true)
    public synchronized void getComputerName(String name) {
        this.mHostName = name;
        
        mSender.asyncSend(Command.DISCONNECT, null);
        
        try {
            mProvider.stop();
        } catch (Exception e) { }
    }
    
    public String getHostName() {
        return this.mHostName;
    }
    
}
