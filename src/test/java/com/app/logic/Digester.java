package com.app.logic;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.security.*;
import java.util.HexFormat;

public class Digester {

    private final static int BUF_SIZE = 1 << 10;
    private final MessageDigest digest;

    public Digester(
            String algorithm
    ) throws NoSuchAlgorithmException {
        this.digest = MessageDigest.getInstance(algorithm);
    }

    public void createContent(
            String filename,
            String content,
            Charset encoding
    ) throws IOException {
        try (BufferedWriter writer =
                     Files.newBufferedWriter(Paths.get(filename), encoding)) {
            writer.write(content);
        }
    }

    public void deleteContent(
            String filename
    ) throws IOException {
        Files.delete(Paths.get(filename));
    }

    public String getHash(
            String filename
    ) throws IOException {
        Path path = Paths.get(filename);
        digest.reset();
        try (InputStream input = Files.newInputStream(path)) {
            byte[] buffer = new byte[BUF_SIZE];
            int read;
            while ((read = input.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            byte[] data = digest.digest();
            return HexFormat.of().formatHex(data);
        }
    }

}
