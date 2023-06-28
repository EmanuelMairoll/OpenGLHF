# OpenGLHF

## Overview

OpenGLHF is a Minecraft hack, developed as a toy project for our Introduction to Computer Graphics course. This project provides an educational exploration into OpenGL shaders and uniforms, utilizing the Lightweight Java Game Library (LWJGL) to interface with OpenGL.

We use the Fabric modding toolchain for Minecraft and the GL33 OpenGL bindings provided by LWJGL. The project's core is the concept of a `Renderer` (implementing the `OpenGLHFRenderer` interface) and the `ShaderProgram` class. Each `Renderer` controls the rendering process of one of the features described below, and links against a `ShaderProgram`, which provides a way to create, compile, and link shaders.

The motivation behind this project was to understand the practical application of OpenGL within a popular game like Minecraft, and learn about shaders and the LWJGL library. Through this project, we gained insights into OpenGL's shader pipeline, learned how to compile and link shaders, and understood how pass data to them.

## Table of Contents 

- [Installation](#installation)
- [Usage](#usage)
- [License](#license)

## Installation

OpenGLHF is a Gradle project. To install the mod, clone the repository and import it into your IDE that supports Gradle. Next, use the Gradle Fabric Loom plugin's `runClient` task to fetch dependencies and build the Minecraft client with the OpenGLHF mod. Execute this task using the command `gradlew fabricloom:runClient`.

## Usage

The OpenGLHF mod provides several visual modifications for entities within the game that can be toggled on and off using key bindings. These visual modifications are managed by their respective rendering classes:

1. `BoxRenderer` - Creates box-like figures for each entity in the game, which can be toggled on and off using keybinding F8.
2. `RectRenderer` - Like BoxRenderer, RectRenderer draws rectangular outlines for each entity, but with a more complex shader implementation, resulting in a 2D bounding box. It can be activated or deactivated by pressing F7.
3. `TraceRenderer` - Renders tracer lines from the player's position to other living entities. TraceRenderer can be enabled or disabled using the F6 key.
   * Also supports an ellipsoid cutout in the center to order to avoid screen cluttering. This can be toggled using the F12 key. The ellipse used by this cutout can be drawn by EllipseRenderer, which is activated or deactivated by the keybinding F10.
4. `InfoPanelRenderer` - Draws a popup that displays information about an entity crucial for PvP, like current an max HP, armor, as well as name and distance to the player. Can be toggled on or off by pressing F9

Most renderer classes extend the `AbstractPosRenderer` class and implement visual effects in different ways. 

Please refer to the respective class code for a more technical understanding of each renderer's implementation.

## License

OpenGLHF is licensed under the MIT License. The MIT License is a permissive free software license originating at the Massachusetts Institute of Technology. It puts only very limited restrictions on reuse and has, therefore, high license compatibility. For the full text of the license, you may visit https://opensource.org/licenses/MIT.
