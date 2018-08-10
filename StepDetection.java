package StepDetection;

public class StepDetection {

    private boolean first_Ax;
    private double prevAx;
    private double prevTime;

    private double previousHeelStrikeTime;
    private double nextHeelStrikeTime;
    private double toeOffTime;
    private double percentStance;

    private int lowerThreshold = 10;
    private int higherThreshold = 20;

    //private int lowerThreshold = 50;
    //private int higherThreshold = 300;

    private boolean firstThresholdUp;
    private boolean secondThresholdUp;
    private boolean secondThresholdDown;
    private boolean firstThresholdDown;
    private boolean nextFirstThresholdUp;
    private boolean nextSecondThresholdUp;


    public StepDetection() {
        this.previousHeelStrikeTime = 0;
        this.nextHeelStrikeTime = 0;
        this.toeOffTime = 0;
        this.percentStance = 0;

        this.first_Ax = true;
        this.prevAx = 0;
        this.prevTime = 0;

        this.firstThresholdUp = false;
        this.secondThresholdUp = false;
        this.secondThresholdDown = false;
        this.firstThresholdDown = false;
        this.nextFirstThresholdUp = false;
        this.nextSecondThresholdUp = false;
    }


    private double calculatePercentStance(double PHS, double NHS, double TO) {
        return 100 * ((TO - PHS) / (NHS - PHS));
    }

    public double getPercentStance() {
        return this.percentStance;
    }


    // (prevAx, prevTime) is the datapoint that occurs before crossing the threshold
    // (afterAx, afterTime) is the datapoint that occurs after crossing the threshold
    private double interpolateLower(double prevAx, double afterAx, double prevTime, double afterTime) {
        double slope;
        double b;

        slope = (afterAx - prevAx) / (afterTime - prevTime);
        b = afterAx - slope*afterTime;

        return (this.lowerThreshold - b) / slope;
    }


    // Returns whether step was detected or not
    public boolean detectStep(double pyr_ax, double time) {

        // CHECK THRESHOLD MILESTONES
        // To ignore first value ~= 400N

        //Log.d("AXIAL", String.valueOf(pyr_ax));
        if (this.first_Ax) {
            this.first_Ax = false;

        } else {
            double slope = pyr_ax - this.prevAx;


            // To account for noise (false positive): rise above first threshold
            if (pyr_ax < lowerThreshold && firstThresholdUp && !secondThresholdUp) {
                this.firstThresholdUp = false;
                this.previousHeelStrikeTime = 0;
            }

            if (pyr_ax < lowerThreshold && firstThresholdUp && secondThresholdUp && secondThresholdDown && firstThresholdDown && nextFirstThresholdUp && !nextSecondThresholdUp) {
                this.nextFirstThresholdUp = false;
                this.nextHeelStrikeTime = 0;
            }


            // MILESTONES
            if (pyr_ax > lowerThreshold && pyr_ax < higherThreshold && slope > 0) {
                if (this.firstThresholdUp && this.secondThresholdUp && this.secondThresholdDown && this.firstThresholdDown && !this.nextFirstThresholdUp) {
                    this.nextHeelStrikeTime = interpolateLower(prevAx, pyr_ax, prevTime, time);
                    this.nextFirstThresholdUp = true;
                } else if (!this.firstThresholdUp){
                    this.previousHeelStrikeTime = interpolateLower(prevAx, pyr_ax, prevTime, time);
                    this.firstThresholdUp = true;
                }
            } else if (pyr_ax > higherThreshold && slope > 0
                    && this.firstThresholdUp) {
                if (this.firstThresholdUp && this.secondThresholdUp && this.secondThresholdDown && this.firstThresholdDown && this.nextFirstThresholdUp) {
                    this.nextSecondThresholdUp = true;
                } else {
                    this.secondThresholdUp = true;
                }
            } else if (pyr_ax > lowerThreshold && pyr_ax < higherThreshold && slope < 0
                    && this.firstThresholdUp && this.secondThresholdUp) {
                this.secondThresholdDown = true;
            } else if (pyr_ax < lowerThreshold && slope < 0
                    && this.firstThresholdUp && this.secondThresholdUp && this.secondThresholdDown && !this.firstThresholdDown) {
                this.toeOffTime = interpolateLower(prevAx, pyr_ax, prevTime, time);
                this.firstThresholdDown = true;
            }


            this.prevAx = pyr_ax;
            this.prevTime = time;
        }


        // Check if candidate step was detected
            if (this.nextSecondThresholdUp) {
                // TODO: do calculations, filter bad steps, and send feedback
                // For now, Log if candidate step was found

                this.percentStance = calculatePercentStance(this.previousHeelStrikeTime, this.nextHeelStrikeTime, this.toeOffTime);

                this.firstThresholdUp = true;        // because we passed first threshold for next step
                this.secondThresholdUp = true;       // because we passed second threshold for next step
                this.secondThresholdDown = false;
                this.firstThresholdDown = false;
                this.nextFirstThresholdUp = false;
                this.nextSecondThresholdUp = false;
                this.previousHeelStrikeTime = this.nextHeelStrikeTime;

                return true;
        }

        return false;

    }


}
