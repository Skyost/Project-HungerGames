package fr.skyost.hungergames.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Joiner;

/**
 * <h1>Skyoconfig</h1>
 * <p><i>Handle configurations with ease !</i></p>
 * <p><b>Current version :</b> v0.2.
 * 
 * @author <b>Skyost</b> (<a href="http://www.skyost.eu">www.skyost.eu</a>).
 * <br>Inspired from <a href="https://forums.bukkit.org/threads/lib-supereasyconfig-v1-2-based-off-of-codename_bs-awesome-easyconfig-v2-1.100569/">SuperEasyConfig</a>.</br>
 */

public class Skyoconfig {
	
	private static final transient char DEFAULT_SEPARATOR = '_';
	private static final transient String LINE_SEPARATOR = System.lineSeparator();
	private static final transient String TEMP_CONFIG_SECTION = "temp";
	
	private static final HashMap<Class<?>, Class<?>> primitivesClass = new HashMap<Class<?>, Class<?>>();{
		primitivesClass.put(int.class, Integer.class);
		primitivesClass.put(long.class, Integer.class);
		primitivesClass.put(double.class, Integer.class);
		primitivesClass.put(float.class, Integer.class);
		primitivesClass.put(boolean.class, Integer.class);
		primitivesClass.put(char.class, Integer.class);
		primitivesClass.put(byte.class, Integer.class);
		primitivesClass.put(void.class, Integer.class);
		primitivesClass.put(short.class, Integer.class);
	}
	
	private transient File configFile;
	private transient List<String> header;
	
	/**
	 * Creates a new instance of Skyoconfig without header.
	 * 
	 * @param configFile The file where the configuration will be loaded an saved.
	 */
	
	protected Skyoconfig(final File configFile) {
		this.configFile = configFile;
	}
	
	/**
	 * Creates a new instance of Skyoconfig.
	 * 
	 * @param configFile The file where the configuration will be loaded an saved.
	 * @param header The configuration's header.
	 */
	
	protected Skyoconfig(final File configFile, final List<String> header) {
		this.configFile = configFile;
		this.header = header;
	}
	
	/**
	 * Loads the configuration from the specified file.
	 * 
	 * @throws InvalidConfigurationException If there is an error while loading the config.
	 */
	
	public final void load() throws InvalidConfigurationException {
		try {
			final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
			for(final Field field : getClass().getFields()) {
				loadField(field, getFieldName(field), config);
			}
			saveConfig(config);
		}
		catch(final Exception ex) {
			throw new InvalidConfigurationException(ex);
		}
	}
	
	/**
	 * Saves the configuration to the specified file.
	 * 
	 * @throws InvalidConfigurationException If there is an error while saving the config.
	 */
	
	public final void save() throws InvalidConfigurationException {
		try {
			final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
			for(final Field field : getClass().getFields()) {
				final String fieldName = getFieldName(field);
				if(config.get(fieldName) == null) {
					saveField(field, fieldName, config);
				}
			}
			saveConfig(config);
		}
		catch(final Exception ex) {
			throw new InvalidConfigurationException(ex);
		}
	}
	
	/**
	 * Gets the formatted <b>Field</b>'s name.
	 * 
	 * @param field The <b>Field</b>.
	 * 
	 * @return The formatted <b>Field</b>'s name.
	 */
	
	private final String getFieldName(final Field field) {
		final ConfigOptions options = field.getAnnotation(ConfigOptions.class);
		return (options == null ? field.getName().replace(DEFAULT_SEPARATOR, '.') : options.name());
	}
	
	/**
	 * Saves the configuration.
	 * 
	 * @param config The <b>YamlConfiguration</b>.
	 * 
	 * @throws IOException <b>InputOutputException</b>.
	 */
	
	private final void saveConfig(final YamlConfiguration config) throws IOException {
		if(header != null && header.size() > 0) {
			config.options().header(Joiner.on(LINE_SEPARATOR).join(header));
		}
		config.save(configFile);
	}
	
	/**
	 * Loads a Field from its path from the config.
	 * 
	 * @param field The specified <b>Field</b>.
	 * @param name The <b>Field</b>'s name. Will be the path.
	 * @param config The <b>YamlConfiguration</b>.
	 * 
	 * @throws IllegalAccessException If <b>Skyoconfig</b> does not have access to the <b>Field</b> or the <b>Method</b> <b>valueOf</b> of a <b>Primitive</b>.
	 * @throws InvocationTargetException Invoked if the <b>Skyoconfig</b> fails to use <b>valueOf</b> for a <b>Primitive</b>.
	 * @throws NoSuchMethodException Same as <b>InvocationTargetException</b>.
	 */
	
	private final void loadField(final Field field, final String name, final YamlConfiguration config) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		final Object value = deserializeField(field, getFieldName(field), config);
		if(value == null) {
			saveField(field, name, config);
		}
		else {
			field.set(this, value);
		}
	}
	
	/**
	 * Saves a <b>Field</b> to the config.
	 * 
	 * @param field The specified <b>Field</b>.
	 * @param name The <b>Field</b>'s name. The path of the value in the config.
	 * @param config The <b>YamlConfiguration</b>.
	 * 
	 * @throws IllegalAccessException If <b>Skyoconfig</b> does not have access to the <b>Field</b>.
	 */
	
	private final void saveField(final Field field, final String name, final YamlConfiguration config) throws IllegalAccessException {
		config.set(name, serializeField(field, config));
	}
	
	/**
	 * Deserializes a <b>Field</b> from the configuration.
	 * 
	 * @param field The specified <b>Field</b>.
	 * @param path The <b>Field</b>'s path in the config.
	 * @param config The <b>YamlConfiguration</b>.
	 * 
	 * @return The deserialied value of the field.
	 * 
	 * @throws ParseException If the JSON parser fails to parse a <b>Location</b> or a <b>Vector</b>.
	 * @throws IllegalAccessException If <b>Skyoconfig</b> does not have access to the <b>Field</b> or the <b>Method</b> <b>valueOf</b> of a <b>Primitive</b>.
	 * @throws InvocationTargetException Invoked if the <b>Skyoconfig</b> fails to use <b>valueOf</b> for a <b>Primitive</b>.
	 * @throws NoSuchMethodException Same as <b>InvocationTargetException</b>.
	 */
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private final Object deserializeField(final Field field, final String path, final YamlConfiguration config) throws ParseException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final Class<?> clazz = field.getType();
		if(Map.class.isAssignableFrom(clazz)) {
			return deserializeMap(config.getConfigurationSection(path));
		}
		final Object value = config.get(path);
		if(value == null || Modifier.isTransient(field.getModifiers())) {
			return null;
		}
		if(clazz.isPrimitive()) {
			return primitivesClass.get(clazz).getMethod("valueOf", String.class).invoke(this, value.toString());
		}
		if(clazz.isEnum()) {
			return Enum.valueOf((Class<? extends Enum>)clazz, value.toString());
		}
		if(List.class.isAssignableFrom(clazz)) {
			return (List<?>)new Yaml().load(value.toString());
		}
		if(Location.class.isAssignableFrom(clazz)) {
			final JSONObject object = (JSONObject)new JSONParser().parse(value.toString());
			return new Location(Bukkit.getWorld(object.get("world").toString()), Double.parseDouble(object.get("x").toString()), Double.parseDouble(object.get("y").toString()), Double.parseDouble(object.get("z").toString()), Float.parseFloat(object.get("yaw").toString()), Float.parseFloat(object.get("pitch").toString()));
		}
		if(Vector.class.isAssignableFrom(clazz)) {
			final JSONObject object = (JSONObject)new JSONParser().parse(value.toString());
			return new Vector(Double.parseDouble(object.get("x").toString()), Double.parseDouble(object.get("y").toString()), Double.parseDouble(object.get("z").toString()));
		}
		return config.get(path);
	}
	
	/**
	 * Deserializes a <b>Map</b> from a <b>ConfigurationSection</b>.
	 * 
	 * @param section The specified <b>ConfigurationSection</b>.
	 * 
	 * @return The deserialied <b>Map</b>.
	 */
	
	private final Map<?, ?> deserializeMap(final ConfigurationSection section) {
		if(section == null) {
			return null;
		}
		final Map<String, Object> result = new HashMap<String, Object>();
		for(final String key : section.getKeys(false)) {
			result.put(key, section.get(key));
		}
		return result;
	}
	
	/**
	 * Serializes a <b>Field</b> to the configuration.
	 * 
	 * @param field The specified <b>Field</b>.
	 * @param config The <b>YamlConfiguration</b>.
	 * 
	 * @return The serialized <b>Field</b>.
	 * 
	 * @throws IllegalAccessException If <b>Skyoconfig</b> does not have access to the <b>Field</b>.
	 */
	
	@SuppressWarnings("unchecked")
	private final Object serializeField(final Field field, final YamlConfiguration config) throws IllegalAccessException {
		final Class<?> clazz = field.getType();
		final Object value = field.get(this);
		if(clazz.isAnnotation()) {
			return null;
		}
		if(Map.class.isAssignableFrom(clazz)) {
			return serializeMap(config, (Map<?, ?>)value);
		}
		if(Location.class.isAssignableFrom(clazz)) {
			final Location location = (Location)value;
			final JSONObject object = new JSONObject();
			object.put("x", location.getX());
			object.put("y", location.getY());
			object.put("z", location.getZ());
			object.put("yaw", location.getYaw());
			object.put("pitch", location.getPitch());
			return object.toJSONString();
		}
		if(Vector.class.isAssignableFrom(clazz)) {
			final Vector vector = (Vector)value;
			final JSONObject object = new JSONObject();
			object.put("x", vector.getX());
			object.put("y", vector.getY());
			object.put("z", vector.getZ());
			return object.toJSONString();
		}
		return value.toString();
	}
	
	/**
	 * Serializes a <b>Map</b> to a <b>ConfigurationSection</b>.
	 * 
	 * @param config The <b>YamlConfiguration</b>.
	 * @param map The specified <b>Map</b>.
	 * 
	 * @return The serialized <b>Map</b> contained into a <b>ConfigurationSection</b>.
	 */
	
	private final ConfigurationSection serializeMap(final YamlConfiguration config, final Map<?, ?> map) {
		final ConfigurationSection section = config.createSection(TEMP_CONFIG_SECTION);
		for(final Entry<?, ?> entry : map.entrySet()) {
			section.set(entry.getKey().toString(), entry.getValue());
		}
		config.set(TEMP_CONFIG_SECTION, null);
		return section;
	}
	
	/**
	 * Gets the configuration's header.
	 * 
	 * @return The header.
	 */
	
	public final List<String> getHeader() {
		return header;
	}
	
	/**
	 * Gets the configuration's <b>File</b>.
	 * 
	 * @return The <b>File</b>.
	 */
	
	public final File getFile() {
		return configFile;
	}
	
	/**
	 * Sets the configuration's header.
	 * 
	 * @param header The header.
	 */
	
	public final void setHeader(final List<String> header) {
		this.header = header;
	}
	
	/**
	 * Sets the configuration's <b>File</b>.
	 * 
	 * @param configFile The <b>File</b>.
	 */
	
	public final void setFile(final File configFile) {
		this.configFile = configFile;
	}
	
	/**
	 * Extra params for configuration fields.
	 */
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	protected @interface ConfigOptions {
		
		/**
		 * The key's name.
		 * 
		 * @return The key's name.
		 */
		
		public String name();
		
	}
	
}
