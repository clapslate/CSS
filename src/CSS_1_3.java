 /** CPU Scheduling Simulator

*Copyright © 2010, Mohit Gvalani

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/




import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;

class CSS_1_3 extends Frame implements ActionListener,FocusListener,ItemListener
{
	Button add = new Button("Add a Process");
	Button remove = new Button("Dump");
	TextField etime = new TextField("Enter time required for its execution");
	TextField atime = new TextField("Enter its arrival time");
	TextField pty = new TextField("Enter its priorty from 1-99999");
	Button pause = new Button("Pause");
	Button start = new Button("Start");
	Label update = new Label("Recent Activity: ");
	TextArea result = new TextArea("Result: ",50,6,TextArea.SCROLLBARS_NONE);
	static Label process = new Label("Process in Execution: ");
	Label l1 = new Label("Report bugs to: mohitindubai@gmail.com");
	TextArea l2 = new TextArea("If you don't want to set priority, leave it AS IT IS. \nIts default value - 0.\n If you don't enter(or haven't entered) priority for 'NPP' or 'PP',\n then that process will have the highest priority",50,6,TextArea.SCROLLBARS_NONE);
	CheckboxGroup algo = new CheckboxGroup();
	Checkbox j1 = new Checkbox("FCFS",algo,true);
	Checkbox j2 = new Checkbox("Round Robin",algo,false);
	Checkbox j3 = new Checkbox("SPN",algo,false);
	Checkbox j4 = new Checkbox("SRT",algo,false);
	Checkbox j5 = new Checkbox("NPP",algo,false);
	Checkbox j6 = new Checkbox("PP",algo,false);
	static Checkbox threeD = new Checkbox("Java 3D effect for Gantt chart",true);
	Gantt gantt = new Gantt();   	 // Drawing Canvas
	java.awt.List Queue = new java.awt.List();
	java.awt.List AQueue = new java.awt.List();
	java.awt.List EQueue = new java.awt.List();
	java.awt.List RQueue = new java.awt.List();
	java.awt.List WQueue = new java.awt.List();
	java.awt.List TQueue = new java.awt.List();

	javax.swing.Timer timer = new javax.swing.Timer(1000,this);   // Timer goes off every second.(1000 ms)

	Vector processes = new Vector(); // process queue
	Vector p2 = new Vector();		 // ready queue
	static Process pro2exec = null;	 // Currently executing process is stored here (pro2exec - Process to execute)
	static int kt = 0;				 //  time keeper (kt - keep time)
	int tleft = 0;					 // indicates remaining execution time 4 a process(FCFS)
	int curr = 0;					 // Points to next process for execution (Round-Robin)
	boolean flagSPN = true;			 // Used as FLAG for Shortest Process Next
	int colorpos = 96;				 // Used for making color labels for processes next to their names

	Font f = new Font("SansSerif",Font.BOLD|Font.ITALIC,16);
	Font fo2 = new Font("SansSerif",Font.BOLD,12);
	Font f3 = new Font("SansSerif",Font.ITALIC,17);
	Font f4 = new Font("SansSerif",Font.ITALIC,10);
	Color c5 = new Color(0,0,0);
	Color c2 = new Color(50,200,50);
	Color c3 = new Color(255,50,50);
	Color c4 = new Color(200,200,200);
	Color c1 = new Color(255,200,0);
	Color c6 = new Color(255,0,0);

	CSS_1_3(String s)
	{
		super(s);
		CSS_1_3 pane = this;
		pane.add(add);
		pane.add(etime);
		pane.add(atime);
		pane.add(pause);
		pane.add(start);
		pane.add(update);
		pane.add(result);
		pane.add(process);
		add(Queue);
		pane.add(j1);
		pane.add(j2);
		pane.add(j3);
		pane.add(j4);						// Adding all components to the Window
		pane.add(j5);
		pane.add(j6);
		add(threeD);
		pane.add(gantt);
		add(AQueue);
		add(WQueue);
		add(RQueue);
		add(TQueue);
		add(EQueue);
		add(l1);
		add(l2);
		add(remove);
		add(pty);
		pane.setVisible(true);

		add.addActionListener(this);
		remove.addActionListener(this);
		start.addActionListener(this);
		pause.addActionListener(this);
		etime.addFocusListener(this);			// Registering this class as the official
		atime.addFocusListener(this);			// "Action Taker" for mouse-clicks,
		pty.addFocusListener(this);				// selection of an item from list,etc.
		Queue.addItemListener(this);
		addWindowListener(new Close());

		etime.setEnabled(false);
		atime.setEnabled(false);
		pty.setEnabled(false);
		pause.setEnabled(false);
		result.setEnabled(false);
		remove.setEnabled(false);

		Queue.add("Process");
		Queue.add("(priority)");
		Queue.add(" ");

		AQueue.add("Arrival");
		AQueue.add("Time");
		AQueue.add(" ");

		EQueue.add("Execution");
		EQueue.add("Time");
		EQueue.add(" ");

		RQueue.add("Response");
		RQueue.add("Time");
		RQueue.add(" ");

		WQueue.add("Waiting");
		WQueue.add("Time");
		WQueue.add(" ");

		TQueue.add("Turnaround");
		TQueue.add("Time");
		TQueue.add(" ");

		for(int i = 3; i < 23; i++)
		{
			RQueue.add(" ");
			WQueue.add(" ");
			TQueue.add(" ");
		}
	}


	public void init()
	{
	}

	public void paint(Graphics g)
	{

		g.drawString("*",352,200);					// *'Conditions Apply' mark
		g.drawString("*",594,465);

		add.setCursor(new Cursor(Cursor.HAND_CURSOR));
		remove.setCursor(new Cursor(Cursor.HAND_CURSOR));
		start.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pause.setCursor(new Cursor(Cursor.HAND_CURSOR));

		add.setFont(fo2);
		result.setFont(fo2);
		remove.setFont(f);
		start.setFont(f);							// Setting fonts
		pause.setFont(f);
		l1.setFont(f);
		l2.setFont(f4);

		l1.setForeground(new Color(200,50,50));
		l2.setForeground(c6);
		start.setForeground(c2);					// Setting colors
		pause.setForeground(c1);
		add.setForeground(c1);

		start.setBackground(c5);
		pause.setBackground(c5);
		l1.setBackground(c4);
		l2.setBackground(c4);
		j1.setBackground(c4);
		j2.setBackground(c4);
		j3.setBackground(c4);
		j4.setBackground(c4);						// Setting background colors
		j5.setBackground(c4);
		j6.setBackground(c4);
		threeD.setBackground(c4);
		add.setBackground(c5);
		update.setBackground(c4);
		result.setBackground(c4);
		process.setBackground(c4);
		gantt.setBackground(new Color(200,200,200));

		add.setSize(120,40);
		remove.setSize(70,40);
		etime.setSize(250,25);
		atime.setSize(250,25);
		pty.setSize(250,25);
		pause.setSize(100,50);
		start.setSize(100,50);
		update.setSize(200,25);
		result.setSize(300,100);
		process.setSize(150,25);
		j1.setSize(75,25);
		j2.setSize(115,25);
		j3.setSize(75,25);
		j4.setSize(75,25);							// Setting sizes
		j5.setSize(75,25);
		j6.setSize(75,25);
		threeD.setSize(200,25);
		gantt.setSize(10000,150);
		Queue.setSize(70,400);
		AQueue.setSize(50,400);
		EQueue.setSize(60,400);
		RQueue.setSize(65,400);
		WQueue.setSize(50,400);
		TQueue.setSize(72,400);
		l1.setSize(900,25);
		l2.setSize(400,125);

		add.setLocation(100,65);
		remove.setLocation(500,350);
		etime.setLocation(100,125);
		atime.setLocation(100,160);
		pty.setLocation(100,195);
		pause.setLocation(300,265);
		start.setLocation(100,265);
		update.setLocation(100,350);
		result.setLocation(100,400);
		process.setLocation(100,375);
		j1.setLocation(450,50);
		j2.setLocation(450,80);
		j3.setLocation(450,110);					// Setting positions of components on
		j4.setLocation(450,140);					// the Window
		j5.setLocation(450,170);
		j6.setLocation(450,200);
		threeD.setLocation(50,700);
		gantt.setLocation(50,540);
		Queue.setLocation(600,50);
		AQueue.setLocation(670,50);
		EQueue.setLocation(720,50);
		RQueue.setLocation(780,50);
		WQueue.setLocation(845,50);
		TQueue.setLocation(895,50);
		l1.setLocation(50,515);
		l2.setLocation(600,460);
	}


	//****-------****----------**** MAIN FUNCTION ****-------****----------****-------
	public static void main(String args[])throws IOException
	{
		CSS_1_3 c = new CSS_1_3("CPU Scheduler Simulator by Mohit Gvalani");
		c.setSize(1000,800);
		c.setVisible(true);
		c.setBackground(new Color(200,200,200));

	}
	//------------------------------------------------------------------------------


	public void actionPerformed(ActionEvent ae)		// This function caters to the interrupt
	//generated by mouse click on "Add a Process","Add->","Start","Stop","Pause","Resume","Dump"
	//Buttons and also to the interrupt generated every second by the Timer.
	{
		System.out.println(ae.getActionCommand());
		if(ae.getActionCommand()=="Add a Process")			// If "Add a Process" Button was clicked
		{
			etime.setEnabled(true);
			atime.setEnabled(true);
			pty.setEnabled(true);
			etime.requestFocus();
			add.setLabel("Add->");
		}
		else if(ae.getActionCommand()=="Add->")				// If "Add->" Button was clicked
		{
			etime.setEnabled(false);
			atime.setEnabled(false);
			pty.setEnabled(false);
			int e = Integer.parseInt(etime.getText());
			int a = Integer.parseInt(atime.getText());
			int pr = 0;
			Process p = new Process(e,a);

			if(pty.getText().equals("Enter its priorty from 1-99999"))
				Queue.add("P"+p.getPid());
			else
			{
				pr = Integer.parseInt(pty.getText());
				Queue.add("P"+p.getPid()+" ("+pr+")");
			}

			p.priority = pr;
			AQueue.add(a+" ");
			EQueue.add(e+" ");
			processes.add(p);
			etime.setText("Enter time required for its execution");
			atime.setText("Enter its arrival time");
			pty.setText("Enter its priorty from 1-99999");
			add.setLabel("Add a Process");
			update.setText("Recent Activity:   Process Added.");
			Graphics g = getGraphics();
			g.setColor(p.getColor());
			g.fill3DRect(570,colorpos,50,15,true);
			colorpos+=15;
			p.pos = Queue.getItemCount()-4;
		}
		else if(ae.getActionCommand()=="Start")				// If "Start" Button was clicked
		{
			start.setLabel("Stop");
			update.setText("Recent Activity:    CPU started.");
			timer.start();
			gantt.refresh();
			pause.setEnabled(true);
			pro2exec = null;
			kt = 0;
			tleft = 0;
			curr = 0;
			flagSPN = true;
			int size = processes.size();
			Process p1 = null;
			p2.removeAllElements();
			for(int i = 0; i<size;i++)
			{
				p1 = (Process)processes.elementAt(i);
				p1.done = false;
				p1.started = false;
				p1.ptl = p1.getEtime();
				p1.rflag = false;
			}
			j1.setEnabled(false);
			j2.setEnabled(false);
			j3.setEnabled(false);
			j4.setEnabled(false);
			j5.setEnabled(false);
			j6.setEnabled(false);
			result.setText("Result:");
			start.setForeground(c3);
		}
		else if(ae.getActionCommand()=="Pause")				// If "Pause" Button was clicked
		{
			pause.setLabel("Resume");
			update.setText("Recent Activity:    CPU paused");
			timer.stop();
		}
		else if(ae.getActionCommand()=="Stop")				// If "Stop" Button was clicked
		{
			start.setLabel("Start");
			update.setText("Recent Activity:    CPU stopped");
			timer.stop();
			pause.setEnabled(false);
			j1.setEnabled(true);
			j2.setEnabled(true);
			j3.setEnabled(true);
			j4.setEnabled(true);
			j5.setEnabled(true);
			j6.setEnabled(true);
			start.setForeground(c2);
		}
		else if(ae.getActionCommand()=="Resume")				// If "Resume" Button was clicked
		{
			pause.setLabel("Pause");
			update.setText("Recent Activity:    CPU resumed");
			timer.start();
		}
		else if(ae.getActionCommand()=="Dump")					// If "Dump" Button wa clicked
		{
			Process p = null;
			int id = Queue.getSelectedIndex();
			for(int i=id-2; i<processes.size(); i++)
			{
					p = (Process)processes.elementAt(i);
					p.pos--;
			}
			System.out.println("Dumping P"+(id-3));
			Queue.remove(id);
			AQueue.remove(id);
			EQueue.remove(id);
			RQueue.remove(id);
			WQueue.remove(id);
			TQueue.remove(id);
			TQueue.add(" ");
			WQueue.add(" ");
			RQueue.add(" ");
			processes.remove(id-3);
			Graphics g = getGraphics();
			colorpos = 96;
			for(int i=0; i<processes.size(); i++)
			{
				p = (Process)processes.elementAt(i);
				g.setColor(p.getColor());
				g.fill3DRect(570,colorpos,50,15,true);
				colorpos+=15;
			}
			g.setColor(c4);
			g.fillRect(570,colorpos,50,15);
			remove.setEnabled(false);
		}
		else												// Timer Interrupt
		{
			System.out.println("TIMER:"+ae.getActionCommand());
			String selected = algo.getSelectedCheckbox().getLabel();
			if(selected.equals("FCFS"))
				FCFS();
			if(selected.equals("Round Robin"))
				RoundRobin();
			if(selected.equals("SPN"))
				SPN();
			if(selected.equals("SRT"))
				SRT();
			if(selected.equals("NPP"))
				NPP();
			if(selected.equals("PP"))
				PP();
		}
	}

	public void keyPressed(KeyEvent ke){}

	public void keyReleased(KeyEvent ke){}

	public void focusGained(FocusEvent fe)
	{
		TextField tf = (TextField)fe.getSource();
		tf.selectAll();
	}

	public void focusLost(FocusEvent fe){}

	public void itemStateChanged(ItemEvent e)
	{
		String sel = Queue.getSelectedItem();
		if(sel!=null)
			remove.setEnabled(true);
	}

	static Process getProcess()
	{
		return pro2exec;
	}

	void processDone()		// When process is done, display its various times(waiting,response & turnaround time)
	{
		pro2exec.done = true;
		pro2exec.ftime = kt;
		pro2exec.setTtime(kt + 1 - pro2exec.getAtime());
		pro2exec.setWtime(pro2exec.getTtime() - pro2exec.getEtime());
		WQueue.replaceItem(String.valueOf(pro2exec.getWtime()),(pro2exec.pos+3));
		RQueue.replaceItem(String.valueOf(pro2exec.getRtime()),(pro2exec.pos+3));
		TQueue.replaceItem(String.valueOf(pro2exec.getTtime()),(pro2exec.pos+3));
	}

	void allProcessesDone()
	{
		timer.stop();
		start.setLabel("Start");
		update.setText("Recent Activity:    CPU stopped");
		result.append(Process.getAvgTimes(processes));
		j1.setEnabled(true);
		j2.setEnabled(true);
		j3.setEnabled(true);
		j4.setEnabled(true);
		j5.setEnabled(true);
		j6.setEnabled(true);
		pause.setEnabled(false);
		start.setForeground(c2);

	}

//------------------------ First Come First Serve Scheduling ----------------------------

	void FCFS()
	{
		int flagdone = 0;
		int mina = 100000;
		int size = processes.size();
		Process p1 = null;
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if((p1.getAtime()<mina)&&(p1.done==false))
			{
				pro2exec = p1;				// Execute the most recently arrived process
				mina = p1.getAtime();
				flagdone = 1;				// FLAG to check is any process is left.
			}
		}
		if((pro2exec.getAtime()<=kt)&&(pro2exec.started==false))
		{
			pro2exec.started=true;			// Start the process
			tleft = pro2exec.getEtime();
		}
		if(pro2exec.started==true)
		{
			if(flagdone == 0)				// If all processes have been finished
				allProcessesDone();
			else							// ELSE continue executing
			{
				tleft--;
				gantt.draw();
				if(tleft==0)
					processDone();
			}
		}
		else								// IDLE time - waiting for a process to arrive
			gantt.skip();
		kt++;
	}

//------------------------- Round Robin Scheduling --------------------------

	void RoundRobin()
	{

		System.out.println("Round Robin "+kt);
		int flagdone = 0,flagdone2 = 0;
		int size = processes.size();
		Process p1 = null;
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if((p1.getAtime()==kt))
			{
				p2.add(p1);			// Adding process to 'READY' queue
				flagdone = 1;
			}
		}
		if(p2.size()!=0)
		{
			curr++;
			System.out.println("curr: "+curr);
			System.out.println("size: "+p2.size());
			curr = curr%p2.size();
			System.out.println("curr: "+curr);

			pro2exec = (Process)p2.elementAt(curr);
			pro2exec.ptl--;
			gantt.draw();
			if(pro2exec.ptl==0)
			{
				processDone();
				p2.remove(curr);
			}
		}
		else
			gantt.skip();
		kt++;
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if((p1.done==false))
			{
				flagdone2 = 1;
			}
		}
		if(flagdone2 == 0)
			allProcessesDone();
}

//---------------------- Shortest Process Next Scheduling -----------------------

	void SPN()
	{
		int flagdone = 0;
		int mine = 100000;
		int size = processes.size();
		Process p1 = null;
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if((p1.getAtime()==kt))
			{
				p2.add(p1);
			}
		}
		if(p2.size()!=0)
		{
			if(flagSPN==true)
			{
				for(int i = 0; i<p2.size();i++)
				{
					p1 = (Process)p2.elementAt(i);
					if(p1.getEtime()<mine)
					{
						mine = p1.getEtime();
						pro2exec = p1;
					}
				}
			}
			flagSPN = false;
			gantt.draw();
			pro2exec.ptl--;
			System.out.println("time left: "+pro2exec.ptl);
			if(pro2exec.ptl==0)
			{
				flagSPN = true;
				processDone();
				p2.remove(pro2exec);
			}
		}
		else
			gantt.skip();
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if(p1.done == false)
				flagdone = 1;
		}
		if(flagdone == 0)
			allProcessesDone();
		kt++;
	}

//-------------------- Shortest Remaining Time Scheduling ---------------------

	void SRT()
	{
		int flagdone = 0;
		int mine = 100000;
		int size = processes.size();
		Process p1 = null;

		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if((p1.getAtime()==kt))
			{
				p2.add(p1);
				flagdone = 1;
			}
		}
		if(p2.size()!=0)
		{
			for(int i = 0; i<p2.size();i++)
			{
				p1 = (Process)p2.elementAt(i);
				if(p1.ptl<mine)
				{
					mine = p1.ptl;
					pro2exec = p1;
				}
			}

			gantt.draw();
			pro2exec.ptl--;
			System.out.println("time left: "+pro2exec.ptl);
			if(pro2exec.ptl==0)
			{
				processDone();
				p2.remove(pro2exec);
			}
		}
		else
			gantt.skip();
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if(p1.done == false)
				flagdone = 1;
		}
		if(flagdone == 0)
			allProcessesDone();
		kt++;
	}

//------------------------ NonPremptive Priority Scheduling ----------------------------

	void NPP()
	{
		int flagdone = 0;
		int minp = 100000;
		int size = processes.size();
		Process p1 = null;
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if((p1.getAtime()==kt))
			{
				p2.add(p1);
			}
		}
		if(p2.size()!=0)
		{
			if(flagSPN==true)
			{
				for(int i = 0; i<p2.size();i++)
				{
					p1 = (Process)p2.elementAt(i);
					if(p1.priority<minp)
					{
						minp = p1.priority;
						pro2exec = p1;
					}
				}
			}
			flagSPN = false;
			gantt.draw();
			pro2exec.ptl--;
			System.out.println("time left: "+pro2exec.ptl);
			if(pro2exec.ptl==0)
			{
				flagSPN = true;
				processDone();
				p2.remove(pro2exec);
			}
		}
		else
			gantt.skip();
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if(p1.done == false)
				flagdone = 1;
		}
		if(flagdone == 0)
			allProcessesDone();
		kt++;
	}

//------------------------- Preemptive Priority Scheduling --------------------------------

	void PP()
	{
		int flagdone = 0;
		int minp = 100000;
		int size = processes.size();
		Process p1 = null;
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if((p1.getAtime()==kt))
			{
				p2.add(p1);
				flagdone = 1;
			}
		}
		if(p2.size()!=0)
		{
			for(int i = 0; i<p2.size();i++)
			{
				p1 = (Process)p2.elementAt(i);
				if(p1.priority<minp)
				{
					minp = p1.priority;
					pro2exec = p1;
				}
			}
			gantt.draw();
			pro2exec.ptl--;
			System.out.println("time left: "+pro2exec.ptl);
			if(pro2exec.ptl==0)
			{
				processDone();
				p2.remove(pro2exec);
			}
		}
		else
			gantt.skip();
		for(int i = 0; i<size;i++)
		{
			p1 = (Process)processes.elementAt(i);
			if(p1.done == false)
				flagdone = 1;
		}
		if(flagdone == 0)
			allProcessesDone();
		kt++;
	}
}

//----------------------- CLASS for CLOSING & OPENING FRAME (CSS WINDOW) -------------------


class Close extends WindowAdapter
{
	//******************** WHEN WINDOW IS CLOSED *************************

	public void windowClosing(WindowEvent we)
	{
		System.exit(0);							// STOP PROGRAM EXECUTION
	}

	//******************** WHEN WINDOW IS OPENED *************************
	public void windowOpened(WindowEvent we){}
}


//------------------------------------ PROCESS CLASS ------------------------------------

class Process
{
	int pid;
	int etime;						// Execution Time
	int atime;						// Arrival Time
	int rtime;						// Response Time
	int wtime;						// Waiting Time
	int ttime;						// Turnaround Time
	int ftime;						// Finish Time
	int ptl;						// Process time left
	int pos;						// Position in List
	boolean done = false;
	boolean started = false;
	boolean rflag = false;
	int priority = 0;
	Color color;

	static int count = 0;
	static int r = 24;				// RGB color for the process
	static int g = 124;
	static int b = 215;
	static float avg_etime = 0;
	static float avg_rtime = 0;
	static float avg_wtime = 0;
	static float avg_ttime = 0;

	Process(int e, int a)
	{
		pid = ++count;
		etime = e;
		atime = a;
		ptl = e;
		r = (r*pid+etime)%255;		// Random Formula for calculating RGB color of process
		g = (g*pid+atime)%255;
		b = (b*pid*etime-atime)%255;
		pos = pid-1;
		color = new Color(r,g,b);
	}

	Process(Process p)
	{
		pid = p.pid;
		etime = p.etime;
		atime = p.atime;
		rtime = p.rtime;
		wtime = p.wtime;
		ttime = p.ttime;
	}

	int getPid()
	{
		return pid;
	}

	int getEtime()
	{
		return etime;
	}

	int getAtime()
	{
		return atime;
	}

	int getWtime()
	{
		return wtime;
	}

	int getRtime()
	{
		return rtime;
	}

	int getTtime()
	{
		return ttime;
	}

	Color getColor()
	{
		return color;
	}

	void setEtime(int i)
	{
		etime = i;
	}

	void setRtime(int i)
	{
		rtime = i;
	}

	void setWtime(int i)
	{
		wtime = i;
	}

	void setTtime(int i)
	{
		ttime = i;
	}

	static String getAvgTimes(Vector p)
	{
		avg_etime = 0;
		avg_rtime = 0;
		avg_wtime = 0;
		avg_ttime = 0;
		int n = p.size();
		Process holder = null;
		for(int i=0; i<n; i++)
		{
			holder = (Process)p.elementAt(i);
			avg_etime += holder.getEtime();
			avg_wtime += holder.getWtime();
			avg_rtime += holder.getRtime();
			avg_ttime += holder.getTtime();
			System.out.println("tt: "+avg_ttime);
		}
		avg_etime /= (float)n;
		avg_wtime /= (float)n;
		avg_rtime /= (float)n;
		avg_ttime /= (float)n;
		System.out.println("avgtt: "+avg_ttime);
		String avg = "\nAverage Execution Time: "+avg_etime+"\nAverage Waiting Time: "+avg_wtime+"\nAverage Response Time: "+avg_rtime+"\nAverage Turnaround Time: "+avg_ttime;
		return avg;
	}
}

//---------------------------- CANVAS - GANTT CHART ------------------------------------

class Gantt extends Canvas
{
	Process pro2draw = null;
	int sec = 30;
	int height = 40;
	int current = 50;
	int top = 50;

//****************** Draing Gantt Chart ********************
	public void draw()
	{
		Graphics g = getGraphics();
		pro2draw = CSS_1_3.getProcess();
		CSS_1_3.process.setText("Process in Execution: P"+pro2draw.getPid());
		if(pro2draw.rflag == false)
		{
			pro2draw.rtime = CSS_1_3.kt - pro2draw.getAtime();
			pro2draw.rflag = true;
		}
		g.setColor(new Color(0,0,0));
		g.setColor(pro2draw.getColor());
		if(CSS_1_3.threeD.getState())
			g.fill3DRect(current,top+30, 30, height,true);
		else
			g.fillRect(current,top+30, 30, height);

		g.setColor(new Color(0,0,0));
		current+=30;
		for(int i = 1; i<300; i++)
			g.drawLine(20+(sec*i),top+70,20+(sec*i),top+60);
		g.drawLine(50,top+70,10000,top+70);
		g.drawString("Gantt Chart",450,20);
	}

//************** Clearing & Resetting Gantt Chart space ****************
	public void refresh()
	{
		current = 50;
		Graphics g = getGraphics();
		g.setColor(new Color(200,200,200));
		g.fillRect(0,0,1000,500);
		g.setColor(new Color(0,0,0));
		for(int i = 1; i<300; i++)
		{
			g.drawLine(20+(sec*i),top+70,20+(sec*i),top+60);
			g.drawString(String.valueOf(i-1),20+(sec*i)-2,top+85);
		}
		g.drawLine(50,top+70,10000,top+70);
		g.drawString("Gantt Chart",450,20);
	}

//******************* IDLE time ********************
	public void skip()
	{
		current+=30;
	}
}