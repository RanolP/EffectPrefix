package me.ranol.effectprefix;

import java.util.ArrayList;
import java.util.List;

import me.ranol.effectprefix.api.PrefixEffect;
import me.ranol.effectprefix.effects.EffXpGet;
import me.ranol.effectprefix.tabcompletor.StringCompletions;

public class TestClass {
	public static void main(String[] args) throws Exception {
		parseTest();
	}

	protected static void parseTest() {
		String parse = "'String type 1' string_type_2 1234 123.4 true";
		int count = 10000;
		System.out.println("##정규식##");
		System.out.println("실험 메시지: " + parse);
		long start = System.currentTimeMillis();
		System.err.println("====" + count + "번 반복 시작====");
		for (int i = 0; i < count; i++) {
			PrefixEffect.parseVars(parse);
		}
		System.err.println("====" + count + "번 반복 종료====");
		System.out.println();
		long used = System.currentTimeMillis() - start;
		System.out.println("걸린 시간:" + used + "ms (" + (used / 1000.0) + "s)");
	}

	protected static void completeTest() {
		StringCompletions q = new StringCompletions("player");
		StringCompletions w = new StringCompletions("is", q);
		StringCompletions e = new StringCompletions("now", w);
		StringCompletions r = new StringCompletions("get", e);
		StringCompletions t = new StringCompletions("prefix", r);
		StringCompletions y = new StringCompletions("test", t);
		String[] tests = { "player", "is", "now", "get", "prefix", "test" };
		System.out.println(y.complete(tests, 6, null));
	}

	protected static void speedTest() throws Exception {
		List<Object> list = new ArrayList<>();
		int loopCount = 100000;
		long start = System.currentTimeMillis();
		System.err.println("====" + loopCount + "번 반복 시작====");
		List<String> messages = new ArrayList<>();
		double j = 0;
		for (int i = 0; i < loopCount; i++) {
			list.clear();
			list.add(j);
			j += 0.1;
			messages.add(PrefixEffect.replaceVars(EffXpGet.class, list)
					.getDescription());
		}
		System.err.println("====" + loopCount + "번 반복 종료====");
		System.out.println();
		System.out.println("총 메시지 갯수: " + messages.size());
		long used = System.currentTimeMillis() - start;
		System.out.println("걸린 시간:" + used + "ms (" + (used / 1000.0) + "s)");
		Thread.sleep(3000);
		for (String msg : messages) {
			System.out.println(msg);
			Thread.sleep(40);
		}
	}
}
