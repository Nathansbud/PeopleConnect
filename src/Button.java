import processing.core.PApplet;

public class Button {
    private PApplet sketch;
    private String text;

    private float x;
    private float y;
    private float width;
    private float height;
    private float curve;

    private float textX;
    private float textY;

    private float textSize = 16; //does this need dynamic scaling?
    private String strokeColor = "ff0000ff";

    private boolean selected;

    Button(PApplet _sketch, String _text, float _x, float _y, float _width, float _height, float _curve) {
        sketch = _sketch;
        text = _text;

        x = _x;
        y = _y;

        sketch.textSize(textSize);
        if(_width > getTextWidth()*1.25f) {
            width = _width;
        } else {
            width = getTextWidth()*1.25f;
        }

        if(x + width > sketch.width) {
            System.out.println("Button Width Overflow Right (" + text + ")");
            x -= Math.abs((x + width) - sketch.width);
        } //Naive, could push to other extreme...

        height = _height;
        curve = _curve;

        textX = (x + 0.5f*width) - 0.5f*getTextWidth();
        textY = (y + 0.75f*height);
    }

    public void draw() {
        strokeColor = isSelected() ? "ffffff00" : "ff0000ff";
        sketch.fill(255);
        sketch.stroke(PApplet.unhex(strokeColor));
        sketch.strokeWeight(2);
        sketch.rect(x, y, width, height, curve);
        sketch.fill(0);
        sketch.textSize(textSize);
        sketch.text(text, textX, textY);

        sketch.strokeWeight(1);
        sketch.stroke(0);
        sketch.textSize(12);
    }

    public boolean isTouched() {
        return sketch.mouseX > x && sketch.mouseX < x+width && sketch.mouseY > y && sketch.mouseY < y+height;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean _selected) {
        selected = _selected;
    }

    public float getX() {
        return x;
    }
    public void setX(float _x) {
        x = _x;
    }
    public float getY() {
        return y;
    }
    public void setY(float _y) {
        y = _y;
    }

    public float getWidth() {
        return width;
    }
    public void setWidth(float _width) {
        width = _width;
    }
    public float getHeight() {
        return height;
    }
    public void setHeight(float _height) {
        height = _height;
    }

    public float getCurve() {
        return curve;
    }
    public void setCurve(float _curve) {
        curve = _curve;
    }

    public void setPosition(float _x, float _y) {
        x = _x;
        y = _y;
    }
    public void setDimensions(float _width, float _height, float _curve) {
        width = _width;
        height = _height;
        curve = _curve;
    }

    public String getText() {
        return text;
    }
    public void setText(String _text) {
        text = _text;
    }

    public float getTextWidth() {
        sketch.textSize(textSize);
        return sketch.textWidth(text);
    }
    public float getTextHeight() {
        return sketch.textAscent() + sketch.textDescent();
    }
}
