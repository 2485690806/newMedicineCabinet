//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package leesche.smartrecycling.base.utils;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBVendorId;
import com.serenegiant.utils.BuildCheck;
import com.serenegiant.utils.HandlerThreadHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class USBMonitor {
	private static final boolean DEBUG = false;
	private static final String TAG = "USBMonitor";
	private static final String ACTION_USB_PERMISSION_BASE = "com.serenegiant.USB_PERMISSION.";
	private final String ACTION_USB_PERMISSION = "com.serenegiant.USB_PERMISSION." + this.hashCode();
	public static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
	private final ConcurrentHashMap<UsbDevice, UsbControlBlock> mCtrlBlocks = new ConcurrentHashMap();
	private final SparseArray<WeakReference<UsbDevice>> mHasPermissions = new SparseArray();
	private final WeakReference<Context> mWeakContext;
	private final UsbManager mUsbManager;
	private final OnDeviceConnectListener mOnDeviceConnectListener;
	private PendingIntent mPermissionIntent = null;
	private List<DeviceFilter> mDeviceFilters = new ArrayList();
	private final Handler mAsyncHandler;
	private volatile boolean destroyed;
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(final Context context, final Intent intent) {
			if (!USBMonitor.this.destroyed) {
				String action = intent.getAction();
				if (USBMonitor.this.ACTION_USB_PERMISSION.equals(action)) {
					synchronized(USBMonitor.this) {
						UsbDevice devicex = (UsbDevice)intent.getParcelableExtra("device");
						if (intent.getBooleanExtra("permission", false)) {
							if (devicex != null) {
								USBMonitor.this.processConnect(devicex);
							}
						} else {
							USBMonitor.this.processCancel(devicex);
						}
					}
				} else {
					UsbDevice device;
					if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action)) {
						device = (UsbDevice)intent.getParcelableExtra("device");
						USBMonitor.this.updatePermission(device, USBMonitor.this.hasPermission(device));
						USBMonitor.this.processAttach(device);
					} else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
						device = (UsbDevice)intent.getParcelableExtra("device");
						if (device != null) {
							UsbControlBlock ctrlBlock = (UsbControlBlock)USBMonitor.this.mCtrlBlocks.remove(device);
							if (ctrlBlock != null) {
								ctrlBlock.close();
							}

							USBMonitor.this.mDeviceCounts = 0;
							USBMonitor.this.processDettach(device);
						}
					}
				}

			}
		}
	};
	private volatile int mDeviceCounts = 0;
	private final Runnable mDeviceCheckRunnable = new Runnable() {
		public void run() {
			if (!USBMonitor.this.destroyed) {
				List<UsbDevice> devices = USBMonitor.this.getDeviceList();
				int n = devices.size();
				int hasPermissionCounts;
				int m;
				synchronized(USBMonitor.this.mHasPermissions) {
					hasPermissionCounts = USBMonitor.this.mHasPermissions.size();
					USBMonitor.this.mHasPermissions.clear();
					Iterator var6 = devices.iterator();

					while(true) {
						if (!var6.hasNext()) {
							m = USBMonitor.this.mHasPermissions.size();
							break;
						}

						UsbDevice device = (UsbDevice)var6.next();
						USBMonitor.this.hasPermission(device);
					}
				}

				if (n > USBMonitor.this.mDeviceCounts || m > hasPermissionCounts) {
					USBMonitor.this.mDeviceCounts = n;
					if (USBMonitor.this.mOnDeviceConnectListener != null) {
						for(int i = 0; i < n; ++i) {
							final UsbDevice devicex = (UsbDevice)devices.get(i);
							USBMonitor.this.mAsyncHandler.post(new Runnable() {
								public void run() {
									USBMonitor.this.mOnDeviceConnectListener.onAttach(devicex);
								}
							});
						}
					}
				}

				USBMonitor.this.mAsyncHandler.postDelayed(this, 2000L);
			}
		}
	};
	private static final int USB_DIR_OUT = 0;
	private static final int USB_DIR_IN = 128;
	private static final int USB_TYPE_MASK = 96;
	private static final int USB_TYPE_STANDARD = 0;
	private static final int USB_TYPE_CLASS = 32;
	private static final int USB_TYPE_VENDOR = 64;
	private static final int USB_TYPE_RESERVED = 96;
	private static final int USB_RECIP_MASK = 31;
	private static final int USB_RECIP_DEVICE = 0;
	private static final int USB_RECIP_INTERFACE = 1;
	private static final int USB_RECIP_ENDPOINT = 2;
	private static final int USB_RECIP_OTHER = 3;
	private static final int USB_RECIP_PORT = 4;
	private static final int USB_RECIP_RPIPE = 5;
	private static final int USB_REQ_GET_STATUS = 0;
	private static final int USB_REQ_CLEAR_FEATURE = 1;
	private static final int USB_REQ_SET_FEATURE = 3;
	private static final int USB_REQ_SET_ADDRESS = 5;
	private static final int USB_REQ_GET_DESCRIPTOR = 6;
	private static final int USB_REQ_SET_DESCRIPTOR = 7;
	private static final int USB_REQ_GET_CONFIGURATION = 8;
	private static final int USB_REQ_SET_CONFIGURATION = 9;
	private static final int USB_REQ_GET_INTERFACE = 10;
	private static final int USB_REQ_SET_INTERFACE = 11;
	private static final int USB_REQ_SYNCH_FRAME = 12;
	private static final int USB_REQ_SET_SEL = 48;
	private static final int USB_REQ_SET_ISOCH_DELAY = 49;
	private static final int USB_REQ_SET_ENCRYPTION = 13;
	private static final int USB_REQ_GET_ENCRYPTION = 14;
	private static final int USB_REQ_RPIPE_ABORT = 14;
	private static final int USB_REQ_SET_HANDSHAKE = 15;
	private static final int USB_REQ_RPIPE_RESET = 15;
	private static final int USB_REQ_GET_HANDSHAKE = 16;
	private static final int USB_REQ_SET_CONNECTION = 17;
	private static final int USB_REQ_SET_SECURITY_DATA = 18;
	private static final int USB_REQ_GET_SECURITY_DATA = 19;
	private static final int USB_REQ_SET_WUSB_DATA = 20;
	private static final int USB_REQ_LOOPBACK_DATA_WRITE = 21;
	private static final int USB_REQ_LOOPBACK_DATA_READ = 22;
	private static final int USB_REQ_SET_INTERFACE_DS = 23;
	private static final int USB_REQ_STANDARD_DEVICE_SET = 0;
	private static final int USB_REQ_STANDARD_DEVICE_GET = 128;
	private static final int USB_REQ_STANDARD_INTERFACE_SET = 1;
	private static final int USB_REQ_STANDARD_INTERFACE_GET = 129;
	private static final int USB_REQ_STANDARD_ENDPOINT_SET = 2;
	private static final int USB_REQ_STANDARD_ENDPOINT_GET = 130;
	private static final int USB_REQ_CS_DEVICE_SET = 32;
	private static final int USB_REQ_CS_DEVICE_GET = 160;
	private static final int USB_REQ_CS_INTERFACE_SET = 33;
	private static final int USB_REQ_CS_INTERFACE_GET = 161;
	private static final int USB_REQ_CS_ENDPOINT_SET = 34;
	private static final int USB_REQ_CS_ENDPOINT_GET = 162;
	private static final int USB_REQ_VENDER_DEVICE_SET = 32;
	private static final int USB_REQ_VENDER_DEVICE_GET = 160;
	private static final int USB_REQ_VENDER_INTERFACE_SET = 33;
	private static final int USB_REQ_VENDER_INTERFACE_GET = 161;
	private static final int USB_REQ_VENDER_ENDPOINT_SET = 34;
	private static final int USB_REQ_VENDER_ENDPOINT_GET = 162;
	private static final int USB_DT_DEVICE = 1;
	private static final int USB_DT_CONFIG = 2;
	private static final int USB_DT_STRING = 3;
	private static final int USB_DT_INTERFACE = 4;
	private static final int USB_DT_ENDPOINT = 5;
	private static final int USB_DT_DEVICE_QUALIFIER = 6;
	private static final int USB_DT_OTHER_SPEED_CONFIG = 7;
	private static final int USB_DT_INTERFACE_POWER = 8;
	private static final int USB_DT_OTG = 9;
	private static final int USB_DT_DEBUG = 10;
	private static final int USB_DT_INTERFACE_ASSOCIATION = 11;
	private static final int USB_DT_SECURITY = 12;
	private static final int USB_DT_KEY = 13;
	private static final int USB_DT_ENCRYPTION_TYPE = 14;
	private static final int USB_DT_BOS = 15;
	private static final int USB_DT_DEVICE_CAPABILITY = 16;
	private static final int USB_DT_WIRELESS_ENDPOINT_COMP = 17;
	private static final int USB_DT_WIRE_ADAPTER = 33;
	private static final int USB_DT_RPIPE = 34;
	private static final int USB_DT_CS_RADIO_CONTROL = 35;
	private static final int USB_DT_PIPE_USAGE = 36;
	private static final int USB_DT_SS_ENDPOINT_COMP = 48;
	private static final int USB_DT_CS_DEVICE = 33;
	private static final int USB_DT_CS_CONFIG = 34;
	private static final int USB_DT_CS_STRING = 35;
	private static final int USB_DT_CS_INTERFACE = 36;
	private static final int USB_DT_CS_ENDPOINT = 37;
	private static final int USB_DT_DEVICE_SIZE = 18;

	public USBMonitor(final Context context, final OnDeviceConnectListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("OnDeviceConnectListener should not null.");
		} else {
			this.mWeakContext = new WeakReference(context);
			this.mUsbManager = (UsbManager)context.getSystemService("usb");
			this.mOnDeviceConnectListener = listener;
			this.mAsyncHandler = HandlerThreadHandler.createHandler("USBMonitor");
			this.destroyed = false;
		}
	}

	public void destroy() {
		this.unregister();
		if (!this.destroyed) {
			this.destroyed = true;
			Set<UsbDevice> keys = this.mCtrlBlocks.keySet();
			if (keys != null) {
				try {
					Iterator var8 = keys.iterator();

					while(var8.hasNext()) {
						UsbDevice key = (UsbDevice)var8.next();
						UsbControlBlock ctrlBlock = (UsbControlBlock)this.mCtrlBlocks.remove(key);
						if (ctrlBlock != null) {
							ctrlBlock.close();
						}
					}
				} catch (Exception var6) {
					Exception e = var6;
					Log.e("USBMonitor", "destroy:", e);
				}
			}

			this.mCtrlBlocks.clear();

			try {
				this.mAsyncHandler.getLooper().quit();
			} catch (Exception var5) {
				Exception e = var5;
				Log.e("USBMonitor", "destroy:", e);
			}
		}

	}

	public synchronized void register() throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			if (this.mPermissionIntent == null) {
				Context context = (Context)this.mWeakContext.get();
				if (context != null) {
					Context wrappedContext = new ContextWrapper(context) {
						@Override
						public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
							// 在 Android 13 (API 33) 及以上版本，强制设置 RECEIVER_NOT_EXPORTED
							if (Build.VERSION.SDK_INT >= 33) {
								return super.registerReceiver(receiver, filter, Context.BIND_NOT_FOREGROUND);
							}
							// 在 Android 12 及以下版本，直接调用原始方法
							return super.registerReceiver(receiver, filter);
						}
					};
					mPermissionIntent = PendingIntent.getBroadcast(wrappedContext, 0, new Intent(ACTION_USB_PERMISSION),  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
					final IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
					// ACTION_USB_DEVICE_ATTACHED never comes on some devices so it should not be added here
					filter.addAction(ACTION_USB_DEVICE_ATTACHED);
					filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
					context.registerReceiver(mUsbReceiver, filter);
				}

				this.mDeviceCounts = 0;
				this.mAsyncHandler.postDelayed(this.mDeviceCheckRunnable, 1000L);
			}

		}
	}

	public synchronized void unregister() throws IllegalStateException {
		this.mDeviceCounts = 0;
		if (!this.destroyed) {
			this.mAsyncHandler.removeCallbacks(this.mDeviceCheckRunnable);
		}

		if (this.mPermissionIntent != null) {
			Context context = (Context)this.mWeakContext.get();

			try {
				if (context != null) {
					context.unregisterReceiver(this.mUsbReceiver);
				}
			} catch (Exception var3) {
				Exception e = var3;
				Log.w("USBMonitor", e);
			}

			this.mPermissionIntent = null;
		}

	}

	public synchronized boolean isRegistered() {
		return !this.destroyed && this.mPermissionIntent != null;
	}

	public void setDeviceFilter(final DeviceFilter filter) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			this.mDeviceFilters.clear();
			this.mDeviceFilters.add(filter);
		}
	}

	public void addDeviceFilter(final DeviceFilter filter) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			this.mDeviceFilters.add(filter);
		}
	}

	public void removeDeviceFilter(final DeviceFilter filter) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			this.mDeviceFilters.remove(filter);
		}
	}

	public void setDeviceFilter(final List<DeviceFilter> filters) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			this.mDeviceFilters.clear();
			this.mDeviceFilters.addAll(filters);
		}
	}

	public void addDeviceFilter(final List<DeviceFilter> filters) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			this.mDeviceFilters.addAll(filters);
		}
	}

	public void removeDeviceFilter(final List<DeviceFilter> filters) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			this.mDeviceFilters.removeAll(filters);
		}
	}

	public int getDeviceCount() throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			return this.getDeviceList().size();
		}
	}

	public List<UsbDevice> getDeviceList() throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			return this.getDeviceList(this.mDeviceFilters);
		}
	}

	public List<UsbDevice> getDeviceList(final List<DeviceFilter> filters) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			HashMap<String, UsbDevice> deviceList = this.mUsbManager.getDeviceList();
			String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/USBCamera/failed_devices.txt";
			File logFile = new File(fileName);
			if (!logFile.getParentFile().exists()) {
				logFile.getParentFile().mkdirs();
			}

			if (!logFile.exists()) {
				try {
					logFile.createNewFile();
				} catch (IOException var20) {
					IOException e = var20;
					e.printStackTrace();
				}
			}

			FileWriter fw = null;
			PrintWriter pw = null;

			try {
				fw = new FileWriter(logFile, true);
			} catch (IOException var19) {
				IOException e = var19;
				e.printStackTrace();
			}

			if (fw != null) {
				pw = new PrintWriter(fw);
			}

			List<UsbDevice> result = new ArrayList();
			if (deviceList != null) {
				if (filters != null && !filters.isEmpty()) {
					Iterator var8 = deviceList.values().iterator();

					label82:
					while(true) {
						while(true) {
							if (!var8.hasNext()) {
								break label82;
							}

							UsbDevice device = (UsbDevice)var8.next();
							Iterator var10 = filters.iterator();

							while(var10.hasNext()) {
								DeviceFilter filter = (DeviceFilter)var10.next();
								if (filter != null && filter.matches(device) || filter != null && filter.mSubclass == device.getDeviceSubclass()) {
									if (!filter.isExclude) {
										result.add(device);
									}
									break;
								}

								String devModel = Build.MODEL;
								String devSystemVersion = VERSION.RELEASE;
								String devClass = String.valueOf(device.getDeviceClass());
								String subClass = String.valueOf(device.getDeviceSubclass());

								try {
									if (pw != null) {
										StringBuilder sb = new StringBuilder();
										sb.append(devModel);
										sb.append("/");
										sb.append(devSystemVersion);
										sb.append(":");
										sb.append("class=" + devClass + ", subclass=" + subClass);
										pw.println(sb.toString());
										pw.flush();
										fw.flush();
									}
								} catch (IOException var18) {
									IOException e = var18;
									e.printStackTrace();
								}
							}
						}
					}
				} else {
					result.addAll(deviceList.values());
				}
			}

			if (pw != null) {
				pw.close();
			}

			if (fw != null) {
				try {
					fw.close();
				} catch (IOException var17) {
					IOException e = var17;
					e.printStackTrace();
				}
			}

			return result;
		}
	}

	public List<UsbDevice> getDeviceList(final DeviceFilter filter) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			HashMap<String, UsbDevice> deviceList = this.mUsbManager.getDeviceList();
			List<UsbDevice> result = new ArrayList();
			if (deviceList != null) {
				Iterator var4 = deviceList.values().iterator();

				while(true) {
					UsbDevice device;
					do {
						if (!var4.hasNext()) {
							return result;
						}

						device = (UsbDevice)var4.next();
					} while(filter != null && (!filter.matches(device) || filter.isExclude));

					result.add(device);
				}
			} else {
				return result;
			}
		}
	}

	public Iterator<UsbDevice> getDevices() throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			Iterator<UsbDevice> iterator = null;
			HashMap<String, UsbDevice> list = this.mUsbManager.getDeviceList();
			if (list != null) {
				iterator = list.values().iterator();
			}

			return iterator;
		}
	}

	public final void dumpDevices() {
		HashMap<String, UsbDevice> list = this.mUsbManager.getDeviceList();
		if (list != null) {
			Set<String> keys = list.keySet();
			if (keys != null && keys.size() > 0) {
				StringBuilder sb = new StringBuilder();
				Iterator var4 = keys.iterator();

				while(var4.hasNext()) {
					String key = (String)var4.next();
					UsbDevice device = (UsbDevice)list.get(key);
					int num_interface = device != null ? device.getInterfaceCount() : 0;
					sb.setLength(0);

					for(int i = 0; i < num_interface; ++i) {
						sb.append(String.format(Locale.US, "interface%d:%s", i, device.getInterface(i).toString()));
					}

					Log.i("USBMonitor", "key=" + key + ":" + device + ":" + sb.toString());
				}
			} else {
				Log.i("USBMonitor", "no device");
			}
		} else {
			Log.i("USBMonitor", "no device");
		}

	}

	public final boolean hasPermission(final UsbDevice device) throws IllegalStateException {
		if (this.destroyed) {
			throw new IllegalStateException("already destroyed");
		} else {
			return this.updatePermission(device, device != null && this.mUsbManager.hasPermission(device));
		}
	}

	private boolean updatePermission(final UsbDevice device, final boolean hasPermission) {
		int deviceKey = getDeviceKey(device, true);
		synchronized(this.mHasPermissions) {
			if (hasPermission) {
				if (this.mHasPermissions.get(deviceKey) == null) {
					this.mHasPermissions.put(deviceKey, new WeakReference(device));
				}
			} else {
				this.mHasPermissions.remove(deviceKey);
			}

			return hasPermission;
		}
	}

	public synchronized boolean requestPermission(final UsbDevice device) {
		boolean result = false;
		if (this.isRegistered()) {
			if (device != null) {
				if (this.mUsbManager.hasPermission(device)) {
					this.processConnect(device);
				} else {
					try {
						this.mUsbManager.requestPermission(device, this.mPermissionIntent);
					} catch (Exception var4) {
						Exception e = var4;
						Log.w("USBMonitor", e);
						this.processCancel(device);
						result = true;
					}
				}
			} else {
				this.processCancel(device);
				result = true;
			}
		} else {
			this.processCancel(device);
			result = true;
		}

		return result;
	}

	public UsbControlBlock openDevice(final UsbDevice device) throws SecurityException {
		if (this.hasPermission(device)) {
			UsbControlBlock result = (UsbControlBlock)this.mCtrlBlocks.get(device);
			if (result == null) {
				result = new UsbControlBlock(this, device);
				this.mCtrlBlocks.put(device, result);
			}

			return result;
		} else {
			throw new SecurityException("has no permission");
		}
	}

	private final void processConnect(final UsbDevice device) {
		if (!this.destroyed) {
			this.updatePermission(device, true);
			this.mAsyncHandler.post(new Runnable() {
				public void run() {
					UsbControlBlock ctrlBlock = (UsbControlBlock)USBMonitor.this.mCtrlBlocks.get(device);
					boolean createNew;
					if (ctrlBlock == null) {
						ctrlBlock = new UsbControlBlock(USBMonitor.this, device);
						USBMonitor.this.mCtrlBlocks.put(device, ctrlBlock);
						createNew = true;
					} else {
						createNew = false;
					}

					if (USBMonitor.this.mOnDeviceConnectListener != null) {
						USBMonitor.this.mOnDeviceConnectListener.onConnect(device, ctrlBlock, createNew);
					}

				}
			});
		}
	}

	private final void processCancel(final UsbDevice device) {
		if (!this.destroyed) {
			this.updatePermission(device, false);
			if (this.mOnDeviceConnectListener != null) {
				this.mAsyncHandler.post(new Runnable() {
					public void run() {
						USBMonitor.this.mOnDeviceConnectListener.onCancel(device);
					}
				});
			}

		}
	}

	private final void processAttach(final UsbDevice device) {
		if (!this.destroyed) {
			if (this.mOnDeviceConnectListener != null) {
				this.mAsyncHandler.post(new Runnable() {
					public void run() {
						USBMonitor.this.mOnDeviceConnectListener.onAttach(device);
					}
				});
			}

		}
	}

	private final void processDettach(final UsbDevice device) {
		if (!this.destroyed) {
			if (this.mOnDeviceConnectListener != null) {
				this.mAsyncHandler.post(new Runnable() {
					public void run() {
						USBMonitor.this.mOnDeviceConnectListener.onDettach(device);
					}
				});
			}

		}
	}

	public static final String getDeviceKeyName(final UsbDevice device) {
		return getDeviceKeyName(device, (String)null, false);
	}

	public static final String getDeviceKeyName(final UsbDevice device, final boolean useNewAPI) {
		return getDeviceKeyName(device, (String)null, useNewAPI);
	}

	@SuppressLint({"NewApi"})
	public static final String getDeviceKeyName(final UsbDevice device, final String serial, final boolean useNewAPI) {
		if (device == null) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(device.getVendorId());
			sb.append("#");
			sb.append(device.getProductId());
			sb.append("#");
			sb.append(device.getDeviceClass());
			sb.append("#");
			sb.append(device.getDeviceSubclass());
			sb.append("#");
			sb.append(device.getDeviceProtocol());
			if (!TextUtils.isEmpty(serial)) {
				sb.append("#");
				sb.append(serial);
			}

			if (useNewAPI && BuildCheck.isAndroid5()) {
				sb.append("#");
				if (TextUtils.isEmpty(serial)) {
					sb.append(device.getSerialNumber());
					sb.append("#");
				}

				sb.append(device.getManufacturerName());
				sb.append("#");
				sb.append(device.getConfigurationCount());
				sb.append("#");
				if (BuildCheck.isMarshmallow()) {
					sb.append(device.getVersion());
					sb.append("#");
				}
			}

			return sb.toString();
		}
	}

	public static final int getDeviceKey(final UsbDevice device) {
		return device != null ? getDeviceKeyName(device, (String)null, false).hashCode() : 0;
	}

	public static final int getDeviceKey(final UsbDevice device, final boolean useNewAPI) {
		return device != null ? getDeviceKeyName(device, (String)null, useNewAPI).hashCode() : 0;
	}

	public static final int getDeviceKey(final UsbDevice device, final String serial, final boolean useNewAPI) {
		return device != null ? getDeviceKeyName(device, serial, useNewAPI).hashCode() : 0;
	}

	private static String getString(final UsbDeviceConnection connection, final int id, final int languageCount, final byte[] languages) {
		byte[] work = new byte[256];
		String result = null;

		for(int i = 1; i <= languageCount; ++i) {
			int ret = connection.controlTransfer(128, 6, 768 | id, languages[i], work, 256, 0);
			if (ret > 2 && work[0] == ret && work[1] == 3) {
				try {
					result = new String(work, 2, ret - 2, "UTF-16LE");
					if (!"Љ".equals(result)) {
						break;
					}

					result = null;
				} catch (UnsupportedEncodingException var9) {
				}
			}
		}

		return result;
	}

	public UsbDeviceInfo getDeviceInfo(final UsbDevice device) {
		return updateDeviceInfo(this.mUsbManager, device, (UsbDeviceInfo)null);
	}

	public static UsbDeviceInfo getDeviceInfo(final Context context, final UsbDevice device) {
		return updateDeviceInfo((UsbManager)context.getSystemService("usb"), device, new UsbDeviceInfo());
	}

	@TargetApi(23)
	public static UsbDeviceInfo updateDeviceInfo(final UsbManager manager, final UsbDevice device, final UsbDeviceInfo _info) {
		UsbDeviceInfo info = _info != null ? _info : new UsbDeviceInfo();
		info.clear();
		if (device != null) {
			if (BuildCheck.isLollipop()) {
				info.manufacturer = device.getManufacturerName();
				info.product = device.getProductName();
				info.serial = device.getSerialNumber();
			}

			if (BuildCheck.isMarshmallow()) {
				info.usb_version = device.getVersion();
			}

			if (manager != null && manager.hasPermission(device)) {
				UsbDeviceConnection connection = manager.openDevice(device);
				if (connection == null) {
					return null;
				}

				byte[] desc = connection.getRawDescriptors();
				if (TextUtils.isEmpty(info.usb_version)) {
					info.usb_version = String.format("%x.%02x", desc[3] & 255, desc[2] & 255);
				}

				if (TextUtils.isEmpty(info.version)) {
					info.version = String.format("%x.%02x", desc[13] & 255, desc[12] & 255);
				}

				if (TextUtils.isEmpty(info.serial)) {
					info.serial = connection.getSerial();
				}

				byte[] languages = new byte[256];
				int languageCount = 0;

				try {
					int result = connection.controlTransfer(128, 6, 768, 0, languages, 256, 0);
					if (result > 0) {
						languageCount = (result - 2) / 2;
					}

					if (languageCount > 0) {
						if (TextUtils.isEmpty(info.manufacturer)) {
							info.manufacturer = getString(connection, desc[14], languageCount, languages);
						}

						if (TextUtils.isEmpty(info.product)) {
							info.product = getString(connection, desc[15], languageCount, languages);
						}

						if (TextUtils.isEmpty(info.serial)) {
							info.serial = getString(connection, desc[16], languageCount, languages);
						}
					}
				} finally {
					connection.close();
				}
			}

			if (TextUtils.isEmpty(info.manufacturer)) {
				info.manufacturer = USBVendorId.vendorName(device.getVendorId());
			}

			if (TextUtils.isEmpty(info.manufacturer)) {
				info.manufacturer = String.format("%04x", device.getVendorId());
			}

			if (TextUtils.isEmpty(info.product)) {
				info.product = String.format("%04x", device.getProductId());
			}
		}

		return info;
	}

	public static final class UsbControlBlock implements Cloneable {
		private final WeakReference<USBMonitor> mWeakMonitor;
		private final WeakReference<UsbDevice> mWeakDevice;
		protected UsbDeviceConnection mConnection;
		protected final UsbDeviceInfo mInfo;
		private final int mBusNum;
		private final int mDevNum;
		private final SparseArray<SparseArray<UsbInterface>> mInterfaces;

		private UsbControlBlock(final USBMonitor monitor, final UsbDevice device) {
			this.mInterfaces = new SparseArray();
			this.mWeakMonitor = new WeakReference(monitor);
			this.mWeakDevice = new WeakReference(device);
			this.mConnection = monitor.mUsbManager.openDevice(device);
			this.mInfo = USBMonitor.updateDeviceInfo(monitor.mUsbManager, device, (UsbDeviceInfo)null);
			String name = device.getDeviceName();
			String[] v = !TextUtils.isEmpty(name) ? name.split("/") : null;
			int busnum = 0;
			int devnum = 0;
			if (v != null) {
				busnum = Integer.parseInt(v[v.length - 2]);
				devnum = Integer.parseInt(v[v.length - 1]);
			}

			this.mBusNum = busnum;
			this.mDevNum = devnum;
			if (this.mConnection != null) {
				int desc = this.mConnection.getFileDescriptor();
				byte[] rawDesc = this.mConnection.getRawDescriptors();
				Log.i("USBMonitor", String.format(Locale.US, "name=%s,desc=%d,busnum=%d,devnum=%d,rawDesc=", name, desc, busnum, devnum) + rawDesc);
			} else {
				Log.e("USBMonitor", "could not connect to device " + name);
			}

		}

		private UsbControlBlock(final UsbControlBlock src) throws IllegalStateException {
			this.mInterfaces = new SparseArray();
			USBMonitor monitor = src.getUSBMonitor();
			UsbDevice device = src.getDevice();
			if (device == null) {
				throw new IllegalStateException("device may already be removed");
			} else {
				this.mConnection = monitor.mUsbManager.openDevice(device);
				if (this.mConnection == null) {
					throw new IllegalStateException("device may already be removed or have no permission");
				} else {
					this.mInfo = USBMonitor.updateDeviceInfo(monitor.mUsbManager, device, (UsbDeviceInfo)null);
					this.mWeakMonitor = new WeakReference(monitor);
					this.mWeakDevice = new WeakReference(device);
					this.mBusNum = src.mBusNum;
					this.mDevNum = src.mDevNum;
				}
			}
		}

		public UsbControlBlock clone() throws CloneNotSupportedException {
			try {
				UsbControlBlock ctrlblock = new UsbControlBlock(this);
				return ctrlblock;
			} catch (IllegalStateException var3) {
				IllegalStateException e = var3;
				throw new CloneNotSupportedException(e.getMessage());
			}
		}

		public USBMonitor getUSBMonitor() {
			return (USBMonitor)this.mWeakMonitor.get();
		}

		public final UsbDevice getDevice() {
			return (UsbDevice)this.mWeakDevice.get();
		}

		public String getDeviceName() {
			UsbDevice device = (UsbDevice)this.mWeakDevice.get();
			return device != null ? device.getDeviceName() : "";
		}

		public int getDeviceId() {
			UsbDevice device = (UsbDevice)this.mWeakDevice.get();
			return device != null ? device.getDeviceId() : 0;
		}

		public String getDeviceKeyName() {
			return USBMonitor.getDeviceKeyName((UsbDevice)this.mWeakDevice.get());
		}

		public String getDeviceKeyName(final boolean useNewAPI) throws IllegalStateException {
			if (useNewAPI) {
				this.checkConnection();
			}

			return USBMonitor.getDeviceKeyName((UsbDevice)this.mWeakDevice.get(), this.mInfo.serial, useNewAPI);
		}

		public int getDeviceKey() throws IllegalStateException {
			this.checkConnection();
			return USBMonitor.getDeviceKey((UsbDevice)this.mWeakDevice.get());
		}

		public int getDeviceKey(final boolean useNewAPI) throws IllegalStateException {
			if (useNewAPI) {
				this.checkConnection();
			}

			return USBMonitor.getDeviceKey((UsbDevice)this.mWeakDevice.get(), this.mInfo.serial, useNewAPI);
		}

		public String getDeviceKeyNameWithSerial() {
			return USBMonitor.getDeviceKeyName((UsbDevice)this.mWeakDevice.get(), this.mInfo.serial, false);
		}

		public int getDeviceKeyWithSerial() {
			return this.getDeviceKeyNameWithSerial().hashCode();
		}

		public synchronized UsbDeviceConnection getConnection() {
			return this.mConnection;
		}

		public synchronized int getFileDescriptor() throws IllegalStateException {
			this.checkConnection();
			return this.mConnection.getFileDescriptor();
		}

		public synchronized byte[] getRawDescriptors() throws IllegalStateException {
			this.checkConnection();
			return this.mConnection.getRawDescriptors();
		}

		public int getVenderId() {
			UsbDevice device = (UsbDevice)this.mWeakDevice.get();
			return device != null ? device.getVendorId() : 0;
		}

		public int getProductId() {
			UsbDevice device = (UsbDevice)this.mWeakDevice.get();
			return device != null ? device.getProductId() : 0;
		}

		public String getUsbVersion() {
			return this.mInfo.usb_version;
		}

		public String getManufacture() {
			return this.mInfo.manufacturer;
		}

		public String getProductName() {
			return this.mInfo.product;
		}

		public String getVersion() {
			return this.mInfo.version;
		}

		public String getSerial() {
			return this.mInfo.serial;
		}

		public int getBusNum() {
			return this.mBusNum;
		}

		public int getDevNum() {
			return this.mDevNum;
		}

		public synchronized UsbInterface getInterface(final int interface_id) throws IllegalStateException {
			return this.getInterface(interface_id, 0);
		}

		public synchronized UsbInterface getInterface(final int interface_id, final int altsetting) throws IllegalStateException {
			this.checkConnection();
			SparseArray<UsbInterface> intfs = (SparseArray)this.mInterfaces.get(interface_id);
			if (intfs == null) {
				intfs = new SparseArray();
				this.mInterfaces.put(interface_id, intfs);
			}

			UsbInterface intf = (UsbInterface)intfs.get(altsetting);
			if (intf == null) {
				UsbDevice device = (UsbDevice)this.mWeakDevice.get();
				int n = device.getInterfaceCount();

				for(int i = 0; i < n; ++i) {
					UsbInterface temp = device.getInterface(i);
					if (temp.getId() == interface_id && temp.getAlternateSetting() == altsetting) {
						intf = temp;
						break;
					}
				}

				if (intf != null) {
					intfs.append(altsetting, intf);
				}
			}

			return intf;
		}

		public synchronized void claimInterface(final UsbInterface intf) {
			this.claimInterface(intf, true);
		}

		public synchronized void claimInterface(final UsbInterface intf, final boolean force) {
			this.checkConnection();
			this.mConnection.claimInterface(intf, force);
		}

		public synchronized void releaseInterface(final UsbInterface intf) throws IllegalStateException {
			this.checkConnection();
			SparseArray<UsbInterface> intfs = (SparseArray)this.mInterfaces.get(intf.getId());
			if (intfs != null) {
				int index = intfs.indexOfValue(intf);
				intfs.removeAt(index);
				if (intfs.size() == 0) {
					this.mInterfaces.remove(intf.getId());
				}
			}

			this.mConnection.releaseInterface(intf);
		}

		public synchronized void close() {
			if (this.mConnection != null) {
				int n = this.mInterfaces.size();

				for(int i = 0; i < n; ++i) {
					SparseArray<UsbInterface> intfs = (SparseArray)this.mInterfaces.valueAt(i);
					if (intfs != null) {
						int m = intfs.size();

						for(int j = 0; j < m; ++j) {
							UsbInterface intf = (UsbInterface)intfs.valueAt(j);
							this.mConnection.releaseInterface(intf);
						}

						intfs.clear();
					}
				}

				this.mInterfaces.clear();
				this.mConnection.close();
				this.mConnection = null;
				USBMonitor monitor = (USBMonitor)this.mWeakMonitor.get();
				if (monitor != null) {
					if (monitor.mOnDeviceConnectListener != null) {
						monitor.mOnDeviceConnectListener.onDisconnect((UsbDevice)this.mWeakDevice.get(), this);
					}

					monitor.mCtrlBlocks.remove(this.getDevice());
				}
			}

		}

		public boolean equals(final Object o) {
			if (o == null) {
				return false;
			} else if (o instanceof UsbControlBlock) {
				UsbDevice device = ((UsbControlBlock)o).getDevice();
				return device == null ? this.mWeakDevice.get() == null : device.equals(this.mWeakDevice.get());
			} else {
				return o instanceof UsbDevice ? o.equals(this.mWeakDevice.get()) : super.equals(o);
			}
		}

		private synchronized void checkConnection() throws IllegalStateException {
			if (this.mConnection == null) {
				throw new IllegalStateException("already closed");
			}
		}
	}

	public static class UsbDeviceInfo {
		public String usb_version;
		public String manufacturer;
		public String product;
		public String version;
		public String serial;

		public UsbDeviceInfo() {
		}

		private void clear() {
			this.usb_version = this.manufacturer = this.product = this.version = this.serial = null;
		}

		public String toString() {
			return String.format("UsbDevice:usb_version=%s,manufacturer=%s,product=%s,version=%s,serial=%s", this.usb_version != null ? this.usb_version : "", this.manufacturer != null ? this.manufacturer : "", this.product != null ? this.product : "", this.version != null ? this.version : "", this.serial != null ? this.serial : "");
		}
	}

	public interface OnDeviceConnectListener {
		void onAttach(UsbDevice device);

		void onDettach(UsbDevice device);

		void onConnect(UsbDevice device, UsbControlBlock ctrlBlock, boolean createNew);

		void onDisconnect(UsbDevice device, UsbControlBlock ctrlBlock);

		void onCancel(UsbDevice device);
	}
}
