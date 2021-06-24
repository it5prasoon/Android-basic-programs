import 'package:flutter/material.dart';

// importing flutter cube package
import 'package:flutter_cube/flutter_cube.dart';

// creating class of stateful widget
class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  // adding necessary objects
  Object earth;

  @override
  void initState() {
    // assigning name to the objects and providing the
    // object's file path (obj file)
    earth = Object(fileName: "assets/earth/earth_ball.obj");
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,

      // creating appbar
      appBar: AppBar(
        centerTitle: true,
        title: Text(
          "Flutter 3D",
          style: TextStyle(
              color: Colors.greenAccent,
              fontWeight: FontWeight.bold,
              fontSize: 25),
        ),
        backgroundColor: Colors.transparent,
        elevation: 0.0,
      ),
      body: Container(
        // providing linear gradient to the
        // background with two colours
        decoration: BoxDecoration(
            gradient: LinearGradient(
                colors: [Colors.blueAccent, Colors.greenAccent],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight)),
        child: Column(children: [
          // adding the earth object
          Expanded(
            child: Cube(
              onSceneCreated: (Scene scene) {
                scene.world.add(earth);
                scene.camera.zoom = 5;
              },
            ),
          ),
        ]),
      ),
    );
  }
}
