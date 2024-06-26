/**
 * Gamepad buttons plugin for Cordova/Phonegap
 *
 * @author Vlad Stirbu
 * Copyright (c) Vlad Stirbu. 2012-2015. All Rights Reserved.
 * Available under the terms of the MIT License.
 *
 */

package com.vladstirbu.cordova;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class Gamepad extends CordovaPlugin {

	private final Integer[] buttons = new Integer[17];
	private final Hashtable<String, Integer> map = new Hashtable<String, Integer>();
	private CallbackContext callback;

	/**
	 * @param cordova The context of the main Activity.
	 * @param webView The associated CordovaWebView.
	 */
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {

		super.initialize(cordova, webView);

		this.map.put("KEYCODE_BUTTON_A", 0);
		this.map.put("KEYCODE_BUTTON_B", 1);
		this.map.put("KEYCODE_BUTTON_X", 2);
		this.map.put("KEYCODE_BUTTON_Y", 3);
		this.map.put("KEYCODE_BUTTON_L1", 4);
		this.map.put("KEYCODE_BUTTON_R1", 5);
		this.map.put("KEYCODE_BUTTON_L2", 6);
		this.map.put("KEYCODE_BUTTON_R2", 7);
		this.map.put("KEYCODE_SPACE", 8);
		this.map.put("KEYCODE_BUTTON_SELECT", 8);
		this.map.put("KEYCODE_ENTER", 9);
		this.map.put("KEYCODE_BUTTON_START", 9);
		this.map.put("KEYCODE_DPAD_UP", 12);
		this.map.put("KEYCODE_DPAD_DOWN", 13);
		this.map.put("KEYCODE_DPAD_LEFT", 14);
		this.map.put("KEYCODE_DPAD_RIGHT", 15);
		this.map.put("KEYCODE_BACK", 16);

		Log.v("GamepadButtons", "initialized");
	}

	public boolean execute(String action, JSONArray args, CallbackContext contextCallback) {
		callback = contextCallback;

		if (action.equals("register")) {
			PluginResult result = new PluginResult(Status.NO_RESULT);
			result.setKeepCallback(true);
			return true;
		} else {
			callback.error("Invalid Action");
			return false;
		}
	}

	@Override
	public Object onMessage(String id, Object data) {
		if (data instanceof KeyEvent && id.equals("gamepad-plugin")) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, processKeyEvent((KeyEvent) data));
			result.setKeepCallback(true);
			callback.sendPluginResult(result);
			return true;
		} else if (data instanceof MotionEvent && id.equals("gamepad-plugin")) {
			PluginResult result = new PluginResult(PluginResult.Status.OK, processMotionEvent((MotionEvent) data));
			result.setKeepCallback(true);
			callback.sendPluginResult(result);
			return true;
		} else {
			return super.onMessage(id, data);
		}
	}

	/*
	 * Processes the event and returns the result to be passed to webview
	 */
	private JSONObject processKeyEvent(KeyEvent event) {
		String key = KeyEvent.keyCodeToString(event.getKeyCode());
		String eventType;
		JSONObject data = new JSONObject();

		if (map.containsKey(key)) {
			if (event.getAction() == 0) {
				eventType = "GamepadButtonDown";
				buttons[map.get(key)] = 1;
			} else {
				eventType = "GamepadButtonUp";
				buttons[map.get(key)] = 0;
			}

			try {
				data.put("type", eventType);
				data.put("button", map.get(key));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return data;
	}

	private JSONObject processMotionEvent(MotionEvent event) {
		JSONObject data = new JSONObject();
		String dpadKey = null;
		String eventType;

		try {
			Log.d("GAMEPAD", "MotionEvent!");
			eventType = "MotionEvent";
			data.put("type", eventType);
			data.put("x", event.getAxisValue(MotionEvent.AXIS_X));
			data.put("y", event.getAxisValue(MotionEvent.AXIS_Y));
			data.put("rx", event.getAxisValue(MotionEvent.AXIS_Z));
			data.put("ry", event.getAxisValue(MotionEvent.AXIS_RZ));
			data.put("xaxis", event.getAxisValue(MotionEvent.AXIS_HAT_X));
			data.put("yaxis", event.getAxisValue(MotionEvent.AXIS_HAT_Y));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
}
