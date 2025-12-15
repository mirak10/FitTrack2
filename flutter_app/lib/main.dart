import 'package:flutter/material.dart';
import 'dart:async';
import 'package:intl/intl.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Workout Timer',
      theme: ThemeData(
        primarySwatch: Colors.deepPurple,
      ),
      home: MainScreen(),
    );
  }
}

class TimerSession {
  DateTime timestamp;
  int durationInSeconds;
  String type;

  TimerSession({
    required this.timestamp,
    required this.durationInSeconds,
    required this.type,
  });
}

class MainScreen extends StatefulWidget {
  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  // Which page we are on (0 = Workouts, 1 = Logs, 2 = Timer)
  int currentPageIndex = 2;
  
  // Stopwatch stuff
  int stopwatchSeconds = 0;
  Timer? stopwatchTimer;
  bool isStopwatchRunning = false;
  
  // Rest timer stuff
  int restSeconds = 30;
  Timer? restTimer;
  bool isRestRunning = false;

  // List to store completed sessions
  List<TimerSession> completedSessions = [];

  @override
  void dispose() {
    if (stopwatchTimer != null) {
      stopwatchTimer!.cancel();
    }
    if (restTimer != null) {
      restTimer!.cancel();
    }
    super.dispose();
  }

  // Start the stopwatch
  void startStopwatch() {
    setState(() {
      isStopwatchRunning = true;
    });
    
    stopwatchTimer = Timer.periodic(Duration(seconds: 1), (timer) {
      setState(() {
        stopwatchSeconds = stopwatchSeconds + 1;
      });
    });
  }

  // Pause the stopwatch
  void pauseStopwatch() {
    setState(() {
      isStopwatchRunning = false;
    });
    
    if (stopwatchTimer != null) {
      stopwatchTimer!.cancel();
    }
  }

  // Reset the stopwatch
  void resetStopwatch() {
    if (stopwatchTimer != null) {
      stopwatchTimer!.cancel();
    }
    
    // Save the session if there was any time
    if (stopwatchSeconds > 0) {
      TimerSession newSession = TimerSession(
        timestamp: DateTime.now(),
        durationInSeconds: stopwatchSeconds,
        type: 'stopwatch',
      );
      
      setState(() {
        completedSessions.insert(0, newSession);
        stopwatchSeconds = 0;
        isStopwatchRunning = false;
      });
    } else {
      setState(() {
        stopwatchSeconds = 0;
        isStopwatchRunning = false;
      });
    }
  }

  // Start the rest timer
  void startRestTimer() {
    setState(() {
      isRestRunning = true;
    });
    
    restTimer = Timer.periodic(Duration(seconds: 1), (timer) {
      if (restSeconds > 0) {
        setState(() {
          restSeconds = restSeconds - 1;
        });
      } else {
        completeRestTimer();
      }
    });
  }

  // Complete the rest timer when it reaches 0
  void completeRestTimer() {
    if (restTimer != null) {
      restTimer!.cancel();
    }
    
    // Save the completed rest session
    TimerSession newSession = TimerSession(
      timestamp: DateTime.now(),
      durationInSeconds: 30 - restSeconds,
      type: 'rest',
    );
    
    setState(() {
      completedSessions.insert(0, newSession);
      restSeconds = 30;
      isRestRunning = false;
    });
  }

  // Cancel the rest timer
  void cancelRestTimer() {
    if (restTimer != null) {
      restTimer!.cancel();
    }
    
    setState(() {
      restSeconds = 30;
      isRestRunning = false;
    });
  }

  // Format time for stopwatch (HH:MM:SS)
  String formatTime(int totalSeconds) {
    int hours = totalSeconds ~/ 3600;
    int minutes = (totalSeconds % 3600) ~/ 60;
    int seconds = totalSeconds % 60;
    
    String hoursStr = hours.toString().padLeft(2, '0');
    String minutesStr = minutes.toString().padLeft(2, '0');
    String secondsStr = seconds.toString().padLeft(2, '0');
    
    return '$hoursStr:$minutesStr:$secondsStr';
  }

  // Format time for rest timer (MM:SS)
  String formatRestTime(int totalSeconds) {
    int minutes = totalSeconds ~/ 60;
    int seconds = totalSeconds % 60;
    
    String minutesStr = minutes.toString().padLeft(2, '0');
    String secondsStr = seconds.toString().padLeft(2, '0');
    
    return '$minutesStr:$secondsStr';
  }

  // Format duration for display in list
  String formatDuration(int seconds) {
    if (seconds < 60) {
      return '${seconds}s';
    } else if (seconds < 3600) {
      int mins = seconds ~/ 60;
      int secs = seconds % 60;
      return '${mins}m ${secs}s';
    } else {
      int hours = seconds ~/ 3600;
      int mins = (seconds % 3600) ~/ 60;
      return '${hours}h ${mins}m';
    }
  }

  // Build the Workouts page
  Widget buildWorkoutsPage() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.fitness_center,
            size: 100,
            color: Colors.deepPurple,
          ),
          SizedBox(height: 20),
          Text(
            'Workouts',
            style: TextStyle(
              fontSize: 32,
              fontWeight: FontWeight.bold,
              color: Colors.black87,
            ),
          ),
          SizedBox(height: 10),
          Text(
            'Your workout plans will appear here',
            style: TextStyle(
              fontSize: 16,
              color: Colors.grey[600],
            ),
          ),
        ],
      ),
    );
  }

  // Build the Logs page
  Widget buildLogsPage() {
    return Column(
      children: [
        // Header
        Container(
          padding: EdgeInsets.all(24),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Completed Sessions',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: Colors.black87,
                ),
              ),
              if (completedSessions.length > 0)
                TextButton.icon(
                  onPressed: () {
                    setState(() {
                      completedSessions.clear();
                    });
                  },
                  icon: Icon(Icons.delete_outline, size: 20),
                  label: Text('Clear All'),
                  style: TextButton.styleFrom(
                    foregroundColor: Colors.red,
                  ),
                ),
            ],
          ),
        ),
        
        // Sessions list
        Expanded(
          child: completedSessions.length == 0
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.history,
                        size: 80,
                        color: Colors.grey[300],
                      ),
                      SizedBox(height: 16),
                      Text(
                        'No completed sessions yet',
                        style: TextStyle(
                          fontSize: 18,
                          color: Colors.grey[600],
                        ),
                      ),
                      SizedBox(height: 8),
                      Text(
                        'Complete a timer session to see it here',
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey[500],
                        ),
                      ),
                    ],
                  ),
                )
              : ListView.builder(
                  padding: EdgeInsets.symmetric(horizontal: 16),
                  itemCount: completedSessions.length,
                  itemBuilder: (context, index) {
                    TimerSession session = completedSessions[index];
                    String dateText = DateFormat('MMM dd, yyyy').format(session.timestamp);
                    String timeText = DateFormat('hh:mm a').format(session.timestamp);
                    
                    return Card(
                      margin: EdgeInsets.only(bottom: 12),
                      elevation: 2,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: ListTile(
                        contentPadding: EdgeInsets.all(16),
                        leading: Container(
                          width: 50,
                          height: 50,
                          decoration: BoxDecoration(
                            color: session.type == 'stopwatch'
                                ? Colors.deepPurple.withOpacity(0.1)
                                : Colors.green.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Icon(
                            session.type == 'stopwatch'
                                ? Icons.timer_outlined
                                : Icons.self_improvement,
                            color: session.type == 'stopwatch'
                                ? Colors.deepPurple
                                : Colors.green,
                            size: 28,
                          ),
                        ),
                        title: Text(
                          session.type == 'stopwatch' ? 'Workout' : 'Rest',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            SizedBox(height: 4),
                            Text(
                              'Duration: ${formatDuration(session.durationInSeconds)}',
                              style: TextStyle(
                                fontSize: 14,
                                color: Colors.grey[700],
                              ),
                            ),
                            SizedBox(height: 2),
                            Text(
                              '$dateText at $timeText',
                              style: TextStyle(
                                fontSize: 12,
                                color: Colors.grey[500],
                              ),
                            ),
                          ],
                        ),
                        trailing: IconButton(
                          icon: Icon(Icons.delete_outline, color: Colors.red),
                          onPressed: () {
                            setState(() {
                              completedSessions.removeAt(index);
                            });
                          },
                        ),
                      ),
                    );
                  },
                ),
        ),
      ],
    );
  }

  // Build the Timer page
  Widget buildTimerPage() {
    return SingleChildScrollView(
      child: Column(
        children: [
          // Top dot indicator
          Container(
            padding: EdgeInsets.symmetric(vertical: 16),
            child: Container(
              width: 8,
              height: 8,
              decoration: BoxDecoration(
                color: Colors.black,
                shape: BoxShape.circle,
              ),
            ),
          ),
          
          // Main timer card
          Container(
            margin: EdgeInsets.all(16),
            padding: EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.05),
                  blurRadius: 10,
                  offset: Offset(0, 2),
                ),
              ],
            ),
            child: Column(
              children: [
                // Stopwatch title
                Text(
                  'Stopwatch',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w500,
                    color: Colors.black87,
                  ),
                ),
                SizedBox(height: 40),
                
                // Stopwatch time display
                Text(
                  formatTime(stopwatchSeconds),
                  style: TextStyle(
                    fontSize: 56,
                    fontWeight: FontWeight.w300,
                    color: Colors.grey[800],
                    letterSpacing: 2,
                  ),
                ),
                SizedBox(height: 32),
                
                // Stopwatch buttons
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                      onPressed: isStopwatchRunning ? null : startStopwatch,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.deepPurple,
                        foregroundColor: Colors.white,
                        padding: EdgeInsets.symmetric(
                          horizontal: 32,
                          vertical: 16,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(24),
                        ),
                      ),
                      child: Text('Start'),
                    ),
                    SizedBox(width: 16),
                    ElevatedButton(
                      onPressed: isStopwatchRunning ? pauseStopwatch : null,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.deepPurple,
                        foregroundColor: Colors.white,
                        padding: EdgeInsets.symmetric(
                          horizontal: 32,
                          vertical: 16,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(24),
                        ),
                      ),
                      child: Text('Pause'),
                    ),
                    SizedBox(width: 16),
                    ElevatedButton(
                      onPressed: resetStopwatch,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.deepPurple,
                        foregroundColor: Colors.white,
                        padding: EdgeInsets.symmetric(
                          horizontal: 32,
                          vertical: 16,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(24),
                        ),
                      ),
                      child: Text('Reset'),
                    ),
                  ],
                ),
                
                SizedBox(height: 48),
                
                // Rest Timer title
                Text(
                  'Rest Timer',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w500,
                    color: Colors.black87,
                  ),
                ),
                SizedBox(height: 16),
                
                // Rest timer display
                Text(
                  formatRestTime(restSeconds),
                  style: TextStyle(
                    fontSize: 56,
                    fontWeight: FontWeight.w300,
                    color: Colors.grey[800],
                    letterSpacing: 2,
                  ),
                ),
                SizedBox(height: 32),
                
                // Rest timer buttons
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                      onPressed: isRestRunning ? null : startRestTimer,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.deepPurple,
                        foregroundColor: Colors.white,
                        padding: EdgeInsets.symmetric(
                          horizontal: 32,
                          vertical: 16,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(24),
                        ),
                      ),
                      child: Text('Start Rest'),
                    ),
                    SizedBox(width: 16),
                    ElevatedButton(
                      onPressed: isRestRunning ? cancelRestTimer : null,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.deepPurple,
                        foregroundColor: Colors.white,
                        padding: EdgeInsets.symmetric(
                          horizontal: 32,
                          vertical: 16,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(24),
                        ),
                      ),
                      child: Text('Cancel Rest'),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // Function to get the current page based on index
  Widget getCurrentPage() {
    if (currentPageIndex == 0) {
      return buildWorkoutsPage();
    } else if (currentPageIndex == 1) {
      return buildLogsPage();
    } else {
      return buildTimerPage();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      body: SafeArea(
        child: getCurrentPage(),
      ),
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 10,
              offset: Offset(0, -2),
            ),
          ],
        ),
        child: BottomNavigationBar(
          currentIndex: currentPageIndex,
          onTap: (index) {
            setState(() {
              currentPageIndex = index;
            });
          },
          selectedItemColor: Colors.deepPurple,
          unselectedItemColor: Colors.grey,
          showSelectedLabels: true,
          showUnselectedLabels: true,
          items: [
            BottomNavigationBarItem(
              icon: Icon(Icons.fitness_center),
              label: 'Workouts',
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.history),
              label: 'Logs',
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.timer),
              label: 'Timer',
            ),
          ],
        ),
      ),
    );
  }
}