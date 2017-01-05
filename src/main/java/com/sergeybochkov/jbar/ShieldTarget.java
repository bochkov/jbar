package com.sergeybochkov.jbar;

import java.util.List;

public interface ShieldTarget {

    void generate(List<Shield> shields);

    void generateNow(Shield shield);
}
