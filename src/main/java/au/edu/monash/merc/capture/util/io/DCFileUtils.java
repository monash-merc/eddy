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
import org.apache.commons.io.FileExistsException;
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
            return Files.move(Paths.get(olderDirName), Paths.get(newDirName), StandardCopyOption.ATOMIC_MOVE);
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
            Path parent = newPath.getParent();
            if (parent != null) {
                PosixFileAttributes attr = Files.readAttributes(parent, PosixFileAttributes.class);
                logger.info("===== parent owner: " + attr.owner() + "  group: " + attr.group() + "  permissions: " + attr.permissions());
                Set<PosixFilePermission> perms = attr.permissions();
                FileAttribute<Set<PosixFilePermission>> fileAttr = PosixFilePermissions.asFileAttribute(perms);
                Path newDir = Files.createDirectory(newPath);
                PosixFileAttributes dirAttr = Files.readAttributes(newDir, PosixFileAttributes.class);
                logger.info("===== new dir owner: " + dirAttr.owner() + "  group: " + dirAttr.group() + "  permissions: " + dirAttr.permissions());

                UserPrincipalLookupService principalLookupService = FileSystems.getDefault().getUserPrincipalLookupService();
                UserPrincipal userPrincipal = principalLookupService.lookupPrincipalByName(attr.owner().getName());
                GroupPrincipal groupPrincipal = principalLookupService.lookupPrincipalByGroupName(attr.group().getName());
                Files.setAttribute(newDir, "posix:owner", userPrincipal, LinkOption.NOFOLLOW_LINKS);
                Files.setAttribute(newDir, "posix:group", groupPrincipal, LinkOption.NOFOLLOW_LINKS);

                PosixFileAttributes lastAttr = Files.readAttributes(newDir, PosixFileAttributes.class);
                logger.info("=====>>>  final new dir owner: " + lastAttr.owner() + "  group: " + lastAttr.group() + "  permissions: " + lastAttr.permissions());
                return newDir;
            } else {
                return Files.createDirectory(newPath);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DCFileException(e);
        }
    }

    public static void creatFileFromSrc(String srcFileName, String destFileName) {
        copyFile(srcFileName, destFileName, false);
    }

    public static Path copyFile(String srcFileName, String destFileName, boolean preserveFileDate) {

        if (srcFileName == null) {
            throw new DCFileException("Source must not be null");
        }
        if (destFileName == null) {
            throw new DCFileException("Destination must not be null");
        }

        try {
            if (preserveFileDate) {
                return Files.copy(Paths.get(srcFileName), Paths.get(destFileName), StandardCopyOption.REPLACE_EXISTING);
            } else {
                return Files.copy(Paths.get(srcFileName), Paths.get(destFileName), StandardCopyOption.ATOMIC_MOVE);
            }
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
            in = new BufferedInputStream(new FileInputStream(fileName));
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

    public static Path moverFile(String srcFileName, String destFileName, boolean override) {
        Path source_path = Paths.get(srcFileName);
        Path dest_path = Paths.get(destFileName);

        try {
            if (override) {
                return Files.move(source_path, dest_path, StandardCopyOption.REPLACE_EXISTING);
            } else {
                return Files.move(source_path, dest_path, StandardCopyOption.ATOMIC_MOVE);
            }
        } catch (Exception e) {
            throw new DCFileException(e);
        }
    }

//    public static void moveFile(String srcFileName, String destFileName, boolean override) {
////        File srcFile = new File(srcFileName);
////        File destFile = new File(destFileName);
////        moveFile(srcFile, destFile, override);
//        moverFile(srcFileName, destFileName);
//    }

//    public static void moveFile(File srcFile, File destFile, boolean override) {
//        try {
//            if (srcFile == null) {
//                throw new NullPointerException("Source file must not be null");
//            }
//
//            if (destFile == null) {
//                throw new NullPointerException("Destination file must not be null");
//            }
//
//            if (!srcFile.exists()) {
//                throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
//            }
//            if (srcFile.isDirectory()) {
//                throw new IOException("Source '" + srcFile + "' is a directory");
//            }
//            if (destFile.exists() && !override) {
//                throw new FileExistsException("Destination '" + destFile + "' already exists");
//            }
//            if (destFile.isDirectory()) {
//                throw new IOException("Destination '" + destFile + "' is a directory");
//            }
//            boolean rename = srcFile.renameTo(destFile);
//            if (!rename) {
//                copyFile(srcFile, destFile, true);
//                if (!srcFile.delete()) {
//                    FileUtils.deleteQuietly(destFile);
//                    throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
//                }
//            }
//        } catch (Exception e) {
//            throw new DCFileException(e);
//        }
//    }

//    public static void moveFile(File srcFile, String destFileName, boolean override) {
//        File destFile = new File(destFileName);
//        moveFile(srcFile, destFile, override);
//    }
}
