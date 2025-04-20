//CURRENTLY UNUSED
/*
import AnimatedActor;
import Vector;

public class Particles {
    //defining variables
    private AnimatedActor template; //the particle itself to be copied
    private Vector[] points; //Bezier curve points to spawn
    private Vector[] arcs; //possible directions (startAngle, endAngle)
    private Vector velocities; //range in speed (minSpeed, maxSpeed)
    private int lifetime; //lifetime of particles in frames
    private int frames; //# of frames to play
    private int currentFrame = 0;
    private int rate; //frames per particle (its backwards)
    private int delay; //in frames

    //acting variables
    private AnimatedActor[] particles;

    public Particles(AnimatedActor template, Vector[] points, Vector[] arcs, Vector velocities, int lifetime, int frames, int rate, int delay, int max) {
        this.template = template;
        this.points = points;
        this.velocities = velocities;
        this.lifetime = lifetime;
        this.arcs = arcs;
        this.frames = frames;
        this.rate = rate;
        this.delay = delay;

        particles = new AnimatedActor[max];
    }

    public void Play() {
        if (particles[particles.length - 1] == null && currentFrame < frames) {
            if (currentFrame % rate == 0) {
                //time to create a new particle
                int index = 0;
                for (int i = 0; i < particles.length; i++) {
                    if (particles[i] == null) {
                        index = i;
                        break;
                    }
                }
            }
        }

        //if ()
    }

    public void KillAll() {
        particles = new AnimatedActor[particles.length];
    }
} */