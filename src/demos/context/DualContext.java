/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

package demos.context;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.AWTGraphicsConfiguration;
import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.sun.opengl.util.GLUT;

/** This demo illustrates the use of the GLDrawable and GLContext APIs
    to create two OpenGL contexts for the same Canvas. The red and
    blue portions of the canvas are drawn with separate OpenGL
    contexts. The front and back buffers of the GLDrawable are swapped
    using the GLDrawable.swapBuffers() API. */

public class DualContext extends Canvas {
	private final GLDrawable drawable;
	private final GLContext context1;
	private final GLContext context2;
	private final GLU glu;
	private final GLUT glut;
	private int repaintNum;

	public DualContext(final GLCapabilities capabilities) {
		super(unwrap((AWTGraphicsConfiguration) GLDrawableFactory.getFactory().chooseGraphicsConfiguration(capabilities,
				null, null)));
		drawable = GLDrawableFactory.getFactory().getGLDrawable(this, capabilities, null);
		context1 = drawable.createContext(null);
		context2 = drawable.createContext(null);
		glu = new GLU();
		glut = new GLUT();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		drawable.setRealized(true);
	}

	@Override
	public void removeNotify() {
		context1.destroy();
		context2.destroy();
		drawable.setRealized(false);
	}

	@Override
	public void paint(final Graphics g) {
		final int width = getWidth();
		final int height = getHeight();
		final int mid = width / 2;
		final String str = "" + ++repaintNum;
		int res = context1.makeCurrent();
		if (res != GLContext.CONTEXT_NOT_CURRENT) {
			clearAndDraw(context1.getGL(), 1, 0, 0, 0, 0, mid, height, str);
			context1.release();
		}

		res = context2.makeCurrent();
		if (res != GLContext.CONTEXT_NOT_CURRENT) {
			clearAndDraw(context2.getGL(), 0, 0, 1, mid, 0, width - mid, height, str);
			context2.release();
		}

		drawable.swapBuffers();
	}

	private void clearAndDraw(final GL gl, final float br, final float bg, final float bb, final int x, final int y,
			final int width, final int height, final String str) {
		gl.glViewport(x, y, width, height);
		gl.glScissor(x, y, width, height);
		gl.glEnable(GL.GL_SCISSOR_TEST);
		gl.glClearColor(br, bg, bb, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		final float length = glut.glutStrokeLengthf(GLUT.STROKE_MONO_ROMAN, str);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(x, x + width, y, y + height);
		gl.glTranslatef(x + (width - length) / 2, y + height / 2, 0);
		glut.glutStrokeString(GLUT.STROKE_ROMAN, str);
	}

	public static void main(final String[] args) {
		final JFrame frame = new JFrame("Dual OpenGL Context Test");
		final DualContext dc = new DualContext(new GLCapabilities());
		frame.getContentPane().add(dc, BorderLayout.CENTER);
		final JButton button = new JButton("Repaint");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				dc.repaint();
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(button, BorderLayout.SOUTH);
		frame.setSize(800, 400);
		frame.setVisible(true);
	}

	private static GraphicsConfiguration unwrap(final AWTGraphicsConfiguration config) {
		if (config == null) return null;
		return config.getGraphicsConfiguration();
	}
}
