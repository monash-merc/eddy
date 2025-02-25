/*
 * Copyright (c) 2010-2011, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package au.edu.monash.merc.capture.util.io;

import au.edu.monash.merc.capture.exception.DCFileException;
import au.edu.monash.merc.capture.util.stage.ScanFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class DCFileUtils {

    private static Logger logger = Logger.getLogger(DCFileUtils.class.getName());

    public static boolean checkWritePermission(String pathName) {
        if (pathName == null) {
            throw new DCFileException("directory name must not be null");
        }
        try {
            Path dirPath = Paths.get(pathName);
            return Files.isWritable(dirPath);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public static void deleteDirectory(String dirName) {
        if (dirName == null) {
            throw new DCFileException("directory name must not be null");
        }
        //  delete a directory includes all files
        Path pathToBeDeleted = Paths.get(dirName);
        try (Stream<Path> paths = Files.walk(pathToBeDeleted)) {
            paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DCFileException(e);
        }
    }

    public static Path moveDirectory(String olderDirName, String newDirName) {
        if (olderDirName == null) {
            throw new DCFileException("old directory name must not be null");
        }
        if (newDirName == null) {
            throw new DCFileException("new directory name must not be null");
        }
        try {
            Path destPath = Files.move(Paths.get(olderDirName), Paths.get(newDirName), StandardCopyOption.REPLACE_EXISTING);
            setOwnership(destPath);
            return destPath;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DCFileException(e);
        }
    }

    public static Path createDirectory(String dirName) {
        if (dirName == null) {
            throw new DCFileException("directory name must not be null");
        }
        try {
            Path newPath = Paths.get(dirName);
            Path createdPath = Files.createDirectory(newPath);
            setOwnership(createdPath);
            return createdPath;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DCFileException(e);
        }
    }

    public static Path copyFile(String srcFileName, String destFileName, boolean preserveFileDate) {

        if (srcFileName == null) {
            throw new DCFileException("Source must not be null");
        }
        if (destFileName == null) {
            throw new DCFileException("Destination must not be null");
        }

        try {
            Path copiedPath = Files.copy(Paths.get(srcFileName), Paths.get(destFileName), StandardCopyOption.REPLACE_EXISTING);
            setOwnership(copiedPath);
            return copiedPath;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DCFileException(e);
        }
    }


    public static void deleteFile(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DCFileException(e);
        }
    }

    public static byte[] readFileToByteArray(String fileName) {
        try {
            return FileUtils.readFileToByteArray(new File(fileName));
        } catch (Exception e) {
            throw new DCFileException(e);
        }
    }

    public static InputStream readFileToInputStream(String fileName) {
        if (fileName == null) {
            throw new DCFileException("file name must not be null");
        }
        InputStream in = null;
        try {
            in = new BufferedInputStream(Files.newInputStream(Paths.get(fileName)));
        } catch (Exception e) {
            throw new DCFileException(e);
        }
        return in;
    }

    public static List<String> discoverFileNames(String stagePath, FilenameFilter filter) {

        if (stagePath == null) {
            throw new DCFileException("directory must not be null");
        }
        File scannedDir = new File(stagePath);

        if (scannedDir.exists() && scannedDir.isFile()) {
            throw new DCFileException("Destination '" + stagePath + "' exists but is a file");
        }

        if (filter == null) {
            filter = new ScanFileFilter();
        }
        String[] fileNames = scannedDir.list(filter);
        List<String> scannedFiles = new ArrayList<String>();

        for (int i = 0; i < fileNames.length; i++) {
            File file = new File(stagePath + File.separator + fileNames[i]);
            if (file.isFile()) {
                scannedFiles.add(file.getName());
            }
        }
        return scannedFiles;
    }

    public static Path moverFile(String srcFileName, String destFileName) {
        Path source_path = Paths.get(srcFileName);
        Path dest_path = Paths.get(destFileName);
        try {
            Path newFilePath = Files.move(source_path, dest_path);
            setOwnership(newFilePath);
            return newFilePath;
        } catch (Exception e) {
            throw new DCFileException(e);
        }
    }

    private static void setOwnership(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            // get parent path ownership (owner and group)
            PosixFileAttributes parentAttr = Files.readAttributes(parent, PosixFileAttributes.class);
            UserPrincipalLookupService principalLookupService = FileSystems.getDefault().getUserPrincipalLookupService();
            // get owner
            UserPrincipal userPrincipal = principalLookupService.lookupPrincipalByName(parentAttr.owner().getName());
            // get group
            GroupPrincipal groupPrincipal = principalLookupService.lookupPrincipalByGroupName(parentAttr.group().getName());
            Set<PosixFilePermission> parentPerms = parentAttr.permissions();
            // set the new path as the same perms as the parent.
            Files.setPosixFilePermissions(filePath, parentPerms);
            // set the ownership for file path
            Files.setAttribute(filePath, "posix:owner", userPrincipal, LinkOption.NOFOLLOW_LINKS);
            // set the group for new folder
            Files.setAttribute(filePath, "posix:group", groupPrincipal, LinkOption.NOFOLLOW_LINKS);
        }
    }

}
