package com.mrcaps;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class JSONTweakerTest {
	@Test
	public void test() throws JSONException {
		Scanner s = new Scanner(getClass().getResourceAsStream("testdata.json")).useDelimiter("\\A");
		JSONObject obj = new JSONObject(s.next());
		
		JSONTweaker tweak = new JSONTweaker(obj);
		
		tweak.mod("arr[0]", "1");
		assertEquals("1", obj.getJSONArray("arr").get(0));
		
		assertEquals("1", tweak.mod("arr[0]", null));
		assertEquals("1", obj.getJSONArray("arr").get(0));
		
		tweak.mod("map.blue", "BLUE");
		assertEquals("BLUE", obj.getJSONObject("map").get("blue"));
		
		//System.out.println(obj.toString(2));
	}
}
