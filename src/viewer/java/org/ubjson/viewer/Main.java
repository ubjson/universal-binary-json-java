package org.ubjson.viewer;

import java.io.IOException;

import org.ubjson.MediaContent;

public class Main {
	private static final UBJRenderer RENDERER = new UBJRenderer();

	public static void main(String[] args) throws IllegalArgumentException,
			IOException {
		RENDERER.render(
				MediaContent.class.getResourceAsStream("MediaContent.ubj"),
				System.out);
	}
}