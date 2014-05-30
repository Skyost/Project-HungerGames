package fr.skyost.hungergames.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MobBarAPI {
	
	private final ReflectionUtils reflection = new ReflectionUtils();
	private static MobBarAPI instance;
	private Map<String, Dragon> dragonMap = new HashMap<String, Dragon>();
	
	private MobBarAPI() {}
	
	public static MobBarAPI getInstance() {
		if(MobBarAPI.instance == null)
			MobBarAPI.instance = new MobBarAPI();
		
		return MobBarAPI.instance;
	}
	
	public void setStatus(Player player, String text, int percent, boolean reset) {
		try {
			if(percent <= 0) {
				percent = 1;
			}
			else if(percent > 100) {
				throw new IllegalArgumentException("percent cannot be greater than 100, percent = " + percent);
			}
			
			Dragon dragon = null;
			if(dragonMap.containsKey(player.getName()) && !reset) {
				dragon = dragonMap.get(player.getName());
			}
			else {
				dragon = new Dragon(text, player.getLocation().add(0, -200, 0), percent);
				Object mobPacket = dragon.getSpawnPacket();
				sendPacket(player, mobPacket);
				dragonMap.put(player.getName(), dragon);
			}
			if(text == null) {
				removeStatus(dragon, player);
			}
			else {
				dragon.setName(text);
				dragon.setHealth(percent);
				Object metaPacket = dragon.getMetaPacket(dragon.getWatcher());
				Object teleportPacket = dragon.getTeleportPacket(player.getLocation().add(0, -200, 0));
				sendPacket(player, metaPacket);
				sendPacket(player, teleportPacket);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ErrorReport.createReport(ex).report();
		}
	}
	
	public void removeStatus(Player player) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
		removeStatus(dragonMap.get(player.getName()), player);
	}
	
	public void removeStatus(Dragon dragon, Player player) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
		Object destroyPacket = dragon.getDestroyPacket();
		sendPacket(player, destroyPacket);
		dragonMap.remove(player.getName());
	}
	
	private void sendPacket(Player player, Object packet) {
		try {
			Object nmsPlayer = reflection.getHandle(player);
			Field con_field = nmsPlayer.getClass().getField("playerConnection");
			Object con = con_field.get(nmsPlayer);
			Method packet_method = reflection.getMethod(con.getClass(), "sendPacket");
			packet_method.invoke(con, packet);
		}
		catch(SecurityException e) {
			e.printStackTrace();
		}
		catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		catch(NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	private class Dragon {
		
		private static final int MAX_HEALTH = 200;
		private int id;
		private int x;
		private int y;
		private int z;
		private int pitch = 0;
		private int yaw = 0;
		private byte xvel = 0;
		private byte yvel = 0;
		private byte zvel = 0;
		private float health;
		private boolean visible = false;
		private String name;
		private Object world;
		
		private Object dragon;
		
		public Dragon(String name, Location loc, int percent) {
			this.name = name;
			this.x = loc.getBlockX();
			this.y = loc.getBlockY();
			this.z = loc.getBlockZ();
			this.health = percent / 100F * MAX_HEALTH;
			this.world = reflection.getHandle(loc.getWorld());
		}
		
		public void setHealth(int percent) {
			this.health = percent / 100F * MAX_HEALTH;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Object getSpawnPacket() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			Class<?> Entity = reflection.getCraftClass("Entity");
			Class<?> EntityLiving = reflection.getCraftClass("EntityLiving");
			Class<?> EntityEnderDragon = reflection.getCraftClass("EntityEnderDragon");
			dragon = EntityEnderDragon.getConstructor(reflection.getCraftClass("World")).newInstance(world);
			
			Method setLocation = reflection.getMethod(EntityEnderDragon, "setLocation", new Class<?>[]{double.class, double.class, double.class, float.class, float.class});
			setLocation.invoke(dragon, x, y, z, pitch, yaw);
			
			Method setInvisible = reflection.getMethod(EntityEnderDragon, "setInvisible", new Class<?>[]{boolean.class});
			setInvisible.invoke(dragon, visible);
			
			Method setCustomName = reflection.getMethod(EntityEnderDragon, "setCustomName", new Class<?>[]{String.class});
			setCustomName.invoke(dragon, name);
			
			Method setHealth = reflection.getMethod(EntityEnderDragon, "setHealth", new Class<?>[]{float.class});
			setHealth.invoke(dragon, health);
			
			Field motX = reflection.getField(Entity, "motX");
			motX.set(dragon, xvel);
			
			Field motY = reflection.getField(Entity, "motX");
			motY.set(dragon, yvel);
			
			Field motZ = reflection.getField(Entity, "motX");
			motZ.set(dragon, zvel);
			
			Method getId = reflection.getMethod(EntityEnderDragon, "getId", new Class<?>[]{});
			this.id = (Integer) getId.invoke(dragon);
			
			Class<?> PacketPlayOutSpawnEntityLiving = reflection.getCraftClass("PacketPlayOutSpawnEntityLiving");
			
			Object packet = PacketPlayOutSpawnEntityLiving.getConstructor(new Class<?>[]{EntityLiving}).newInstance(dragon);
			
			return packet;
		}
		
		public Object getDestroyPacket() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
			Class<?> PacketPlayOutEntityDestroy = reflection.getCraftClass("PacketPlayOutEntityDestroy");
			
			Object packet = PacketPlayOutEntityDestroy.getConstructor().newInstance();
			
			Field a = PacketPlayOutEntityDestroy.getDeclaredField("a");
			a.setAccessible(true);
			a.set(packet, new int[]{id});
			
			return packet;
		}
		
		public Object getMetaPacket(Object watcher) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			Class<?> DataWatcher = reflection.getCraftClass("DataWatcher");
			
			Class<?> PacketPlayOutEntityMetadata = reflection.getCraftClass("PacketPlayOutEntityMetadata");
			
			Object packet = PacketPlayOutEntityMetadata.getConstructor(new Class<?>[]{int.class, DataWatcher, boolean.class}).newInstance(id, watcher, true);
			
			return packet;
		}
		
		public Object getTeleportPacket(Location loc) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			Class<?> PacketPlayOutEntityTeleport = reflection.getCraftClass("PacketPlayOutEntityTeleport");
			
			Object packet = PacketPlayOutEntityTeleport.getConstructor(new Class<?>[]{int.class, int.class, int.class, int.class, byte.class, byte.class}).newInstance(this.id, loc.getBlockX() * 32, loc.getBlockY() * 32, loc.getBlockZ() * 32, (byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360));
			
			return packet;
		}
		
		public Object getWatcher() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			Class<?> Entity = reflection.getCraftClass("Entity");
			Class<?> DataWatcher = reflection.getCraftClass("DataWatcher");
			
			Object watcher = DataWatcher.getConstructor(new Class<?>[]{Entity}).newInstance(dragon);
			
			Method a = reflection.getMethod(DataWatcher, "a", new Class<?>[]{int.class, Object.class});
			
			a.invoke(watcher, 0, visible ? (byte) 0 : (byte) 0x20);
			a.invoke(watcher, 6, (Float) health);
			a.invoke(watcher, 7, (Integer) 0);
			a.invoke(watcher, 8, (Byte) (byte) 0);
			a.invoke(watcher, 10, name);
			a.invoke(watcher, 11, (Byte) (byte) 1);
			return watcher;
		}
		
	}
	
	public class ReflectionUtils {
		
		public void sendPacketRadius(Location loc, int radius, Object packet) {
			for(Player p : loc.getWorld().getPlayers()) {
				if(loc.distanceSquared(p.getLocation()) < (radius * radius)) {
					sendPacket(p, packet);
				}
			}
		}
		
		public void sendPacket(List<Player> players, Object packet) {
			for(Player p : players) {
				sendPacket(p, packet);
			}
		}
		
		public void sendPacket(Player p, Object packet) {
			try {
				Object nmsPlayer = getHandle(p);
				Field con_field = nmsPlayer.getClass().getField("playerConnection");
				Object con = con_field.get(nmsPlayer);
				Method packet_method = getMethod(con.getClass(), "sendPacket");
				packet_method.invoke(con, packet);
			}
			catch(SecurityException e) {
				e.printStackTrace();
			}
			catch(IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
			}
			catch(NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		
		public Class<?> getCraftClass(String ClassName) {
			String name = Bukkit.getServer().getClass().getPackage().getName();
			String version = name.substring(name.lastIndexOf('.') + 1) + ".";
			String className = "net.minecraft.server." + version + ClassName;
			Class<?> c = null;
			try {
				c = Class.forName(className);
			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			return c;
		}
		
		public Object getHandle(Entity entity) {
			Object nms_entity = null;
			Method entity_getHandle = getMethod(entity.getClass(), "getHandle");
			try {
				nms_entity = entity_getHandle.invoke(entity);
			}
			catch(IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
			}
			return nms_entity;
		}
		
		public Object getHandle(Object entity) {
			Object nms_entity = null;
			Method entity_getHandle = getMethod(entity.getClass(), "getHandle");
			try {
				nms_entity = entity_getHandle.invoke(entity);
			}
			catch(IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
			}
			return nms_entity;
		}
		
		public Field getField(Class<?> cl, String field_name) {
			try {
				Field field = cl.getDeclaredField(field_name);
				return field;
			}
			catch(SecurityException e) {
				e.printStackTrace();
			}
			catch(NoSuchFieldException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public Method getMethod(Class<?> cl, String method, Class<?>[] args) {
			for(Method m : cl.getMethods()) {
				if(m.getName().equals(method) && ClassListEqual(args, m.getParameterTypes())) {
					return m;
				}
			}
			return null;
		}
		
		public Method getMethod(Class<?> cl, String method, Integer args) {
			for(Method m : cl.getMethods()) {
				if(m.getName().equals(method) && args.equals(Integer.valueOf(m.getParameterTypes().length))) {
					return m;
				}
			}
			return null;
		}
		
		public Method getMethod(Class<?> cl, String method) {
			for(Method m : cl.getMethods()) {
				if(m.getName().equals(method)) {
					return m;
				}
			}
			return null;
		}
		
		public void setValue(Object instance, String fieldName, Object value) throws Exception {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		}
		
		public Object getValue(Object instance, String fieldName) throws Exception {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(instance);
		}
		
		public boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
			boolean equal = true;
			
			if(l1.length != l2.length)
				return false;
			for(int i = 0; i < l1.length; i++) {
				if(l1[i] != l2[i]) {
					equal = false;
					break;
				}
			}
			
			return equal;
		}
	}
	
}
