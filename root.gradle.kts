/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

plugins {
    kotlin("jvm") version "1.6.10" apply false
    id("net.kyori.blossom") version "1.3.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("gg.essential.multi-version.root")
}

preprocess {
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")
    val forge11202 = createNode("1.12.2-forge", 11202, "srg")
    val forge11602 = createNode("1.16.2-forge", 11602, "srg")
    val fabric11602 = createNode("1.16.2-fabric", 11602, "yarn")
    val forge11701 = createNode("1.17.1-forge", 11701, "srg")
    val fabric11701 = createNode("1.17.1-fabric", 11701, "yarn")
    val forge11801 = createNode("1.18.1-forge", 11801, "srg")
    val fabric11801 = createNode("1.18.1-fabric", 11801, "yarn")
    val fabric11900 = createNode("1.19.0-fabric", 11900, "yarn")
    val forge11904 = createNode("1.19.4-forge", 11904, "srg")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "yarn")
    val fabric12000 = createNode("1.20.0-fabric", 12000, "yarn")

    forge11202.link(forge10809)
    forge11602.link(forge11202)
    fabric11602.link(forge11602)
    forge11701.link(forge11602)
    fabric11701.link(fabric11602)
    forge11801.link(forge11701)
    fabric11801.link(fabric11701)
    fabric11900.link(fabric11801)
    forge11904.link(forge11801)
    fabric11904.link(fabric11900)
    fabric12000.link(fabric11904)
}