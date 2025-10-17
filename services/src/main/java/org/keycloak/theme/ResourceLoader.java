package org.keycloak.theme;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public class ResourceLoader {

    public static InputStream getResourceAsStream(String root, String resource) throws IOException {
        if (root == null || resource == null) {
            return null;
        }
        Path rootPath = Path.of("/", root).normalize().toAbsolutePath();
        Path resourcePath = rootPath.resolve(resource).normalize().toAbsolutePath();
        if (resourcePath.startsWith(rootPath)) {
            if (File.separatorChar == '/') {
                resource = resourcePath.toString().substring(1);
            } else {
                resource = resourcePath.toString().substring(2).replace('\\', '/');
            }
            URL url = classLoader().getResource(resource);
            return url != null ? url.openStream() : null;
        } else {
            return null;
        }
    }

    public static InputStream getFileAsStream(File root, String resource) throws IOException {
        // Disallow absolute paths and path traversal sequences in resource
        if (resource == null || resource.contains("..") || resource.startsWith(File.separator) || resource.startsWith("/") || resource.startsWith("\\")) {
            return null;
        }
        File file = getFile(root, resource);
        // Only allow opening local files inside root directory
        if (file != null && file.isFile()) {
            // Defensive: only open stream if actual path starts with root path
            Path rootPath = root.toPath().toAbsolutePath().normalize();
            Path filePath = file.toPath().toAbsolutePath().normalize();
            if (filePath.startsWith(rootPath)) {
                return new java.io.FileInputStream(file);
            }
        }
        return null;
    }

    public static File getFile(File root, String resource) throws IOException {
        if (root == null || resource == null) {
            return null;
        }
        // Disallow absolute paths and traversal attempts in resource string
        if (resource.contains("..") || resource.startsWith(File.separator) || resource.startsWith("/") || resource.startsWith("\\")) {
            return null;
        }
        Path rootPath = root.toPath().toAbsolutePath().normalize();
        Path resourcePath = rootPath.resolve(resource).normalize().toAbsolutePath();
        if (resourcePath.startsWith(rootPath)) {
            return resourcePath.toFile();
        } else {
            return null;
        }
    }

    private static ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
