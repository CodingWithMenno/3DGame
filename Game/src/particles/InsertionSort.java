package particles;

import particles.Particle;

import java.util.List;

//Class for sorting the particles with the insertionSort algorithm
public class InsertionSort {

    public static void sortHighToLow(List<Particle> list) {
        for (int i = 1; i < list.size(); i++) {
            Particle item = list.get(i);
            if (item.getDistanceFromCamera() > list.get(i - 1).getDistanceFromCamera()) {
                sortUpHighToLow(list, i);
            }
        }
    }

    private static void sortUpHighToLow(List<Particle> list, int i) {
        Particle item = list.get(i);
        int attemptPos = i - 1;
        while (attemptPos != 0 && list.get(attemptPos - 1).getDistanceFromCamera() < item.getDistanceFromCamera()) {
            attemptPos--;
        }
        list.remove(i);
        list.add(attemptPos, item);
    }
}
