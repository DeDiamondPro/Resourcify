/*
 * This file is part of Resourcify
 * Copyright (C) 2025 DeDiamondPro
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.buildsource;

import org.gradle.api.Project;

import javax.management.openmbean.SimpleType;

public class Platform {
    private final int major;
    private final int minor;
    private final int patch;
    private final Loader loader;
    private final int version;

    public Platform(int major, int minor, int patch, Loader loader) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.loader = loader;
        this.version = major * 10_000 + minor * 100 + patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public int getVersion() {
        return version;
    }

    public String getVersionString() {
        return major + "." + minor + "." + patch;
    }

    public Loader getLoader() {
        return loader;
    }

    public String getLoaderString() {
        return loader.toString().toLowerCase();
    }

    public boolean isFabric() {
        return loader == Loader.FABRIC;
    }

    public boolean isForge() {
        return loader == Loader.FORGE;
    }

    public boolean isNeoForge() {
        return loader == Loader.NEOFORGE;
    }

    public boolean isForgeLike() {
        return loader.isForgeLike();
    }

    public String getName() {
        return getVersionString() + "-" + loader.toString().toLowerCase();
    }

    public static Platform fromProject(Project project) {
        String[] nameSplit = project.getName().split("-");
        String[] versionSplit = nameSplit[0].split("\\.");
        int major = Integer.parseInt(versionSplit[0]);
        int minor = Integer.parseInt(versionSplit[1]);
        int patch = Integer.parseInt(versionSplit[2]);
        Loader loader = Loader.valueOf(nameSplit[1].toUpperCase());
        return new Platform(major, minor, patch, loader);
    }
}
