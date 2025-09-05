## Summary
### Summary: Sorting Algorithms in Java

Java provides several built-in sorting algorithms, each with its own characteristics and use cases. Here’s a comprehensive overview of the most commonly used sorting algorithms in Java, including their implementation and time complexity:

#### 1. **Quicksort (Primitive Arrays)**
- **Usage**: `Arrays.sort()` for primitive arrays (e.g., `int[]`, `double[]`).
- **Time Complexity**: Average case is \(O(n \log n)\), but can degrade to \(O(n^2)\) in the worst case.
- **Example**:
  ```java
  int[] array = {3, 6, 8, 10, 1, 2, 1};
  Arrays.sort(array);
  System.out.println(Arrays.toString(array)); // Output: [1, 1, 1, 2, 3, 6, 8]
  ```

#### 2. **Merge Sort (Object Arrays)**
- **Usage**: `Arrays.sort()` for object arrays.
- **Time Complexity**: Always \(O(n \log n)\).
- **Example**:
  ```java
  String[] array = {"banana", "apple", "cherry"};
  Arrays.sort(array);
  System.out.println(Arrays.toString(array)); // Output: [apple, banana, cherry]
  ```

#### 3. **Bubble Sort**
- **Description**: Repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order.
- **Time Complexity**: \(O(n^2)\) in both average and worst cases.
- **Example**:
  ```java
  public static void bubbleSort(int[] array) {
      int n = array.length;
      boolean swapped;
      for (int i = 0; i < n - 1; i++) {
          swapped = false;
          for (int j = 0; j < n - 1 - i; j++) {
              if (array[j] > array[j + 1]) {
                  int temp = array[j];
                  array[j] = array[j + 1];
                  array[j + 1] = temp;
                  swapped = true;
              }
          }
          if (!swapped) break;
      }
  }

  int[] array = {64, 34, 25, 12, 22, 11, 90};
  bubbleSort(array);
  System.out.println(Arrays.toString(array)); // Output: [11, 12, 22, 25, 34, 64, 90]
  ```

#### 4. **Insertion Sort**
- **Description**: Builds the final sorted array one item at a time by comparing each element with the ones before it and inserting it into its correct position.
- **Time Complexity**: \(O(n^2)\) in both average and worst cases.
- **Example**:
  ```java
  public static void insertionSort(int[] array) {
      for (int i = 1; i < array.length; i++) {
          int key = array[i];
          int j = i - 1;
          while (j >= 0 && array[j] > key) {
              array[j + 1] = array[j];
              j = j - 1;
          }
          array[j + 1] = key;
      }
  }

  int[] array = {12, 11, 13, 5, 6};
  insertionSort(array);
  System.out.println(Arrays.toString(array)); // Output: [5, 6, 11, 12, 13]
  ```

#### 5. **Selection Sort**
- **Description**: Divides the input list into two parts: the sublist of items already sorted, which is built up from left to right at the front (left) of the list, and the sublist of items remaining to be sorted that occupy the rest of the list.
- **Time Complexity**: \(O(n^2)\) in both average and worst cases.
- **Example**:
  ```java
  public static void selectionSort(int[] array) {
      for (int i = 0; i < array.length - 1; i++) {
          int minIndex = i;
          for (int j = i + 1; j < array.length; j++) {
              if (array[j] < array[minIndex]) {
                  minIndex = j;
              }
          }
          int temp = array[minIndex];
          array[minIndex] = array[i];
          array[i] = temp;
      }
  }

  int[] array = {64, 25, 12, 22, 11};
  selectionSort(array);
  System.out.println(Arrays.toString(array)); // Output: [11, 12, 22, 25, 64]
  ```

#### 6. **Merge Sort**
- **Description**: A divide-and-conquer algorithm that divides the unsorted list into n sublists, each containing one element, and repeatedly merges sublists to produce new sorted sublists until there is only one sublist remaining.
- **Time Complexity**: Always \(O(n \log n)\).
- **Example**:
  ```java
  public static void mergeSort(int[] array, int left, int right) {
      if (left < right) {
          int mid = (left + right) / 2;
          mergeSort(array, left, mid);
          mergeSort(array, mid + 1, right);
          merge(array, left, mid, right);
      }
  }

  private static void merge(int[] array, int left, int mid, int right) {
      int n1 = mid - left + 1;
      int n2 = right - mid;

      int[] L = new int[n1];
      int[] R = new int[n2];

      for (int i = 0; i < n1; ++i)
          L[i] = array[left + i];
      for (int j = 0; j < n2; ++j)
          R[j] = array[mid + 1 + j];

      int i = 0, j = 0, k = left;
      while (i < n1 && j < n2) {
          if (L[i] <= R[j]) {
              array[k] = L[i];
              i++;
          } else {
              array[k] = R[j];
              j++;
          }
          k++;
      }

      while (i < n1) {
          array[k] = L[i];
          i++;
          k++;
      }

      while (j < n2) {
          array[k] = R[j];
          j++;
          k++;
      }
  }

  int[] array = {12, 11, 13, 5, 6};
  mergeSort(array, 0, array.length - 1);
  System.out.println(Arrays.toString(array)); // Output: [5, 6, 11, 12, 13]
  ```

#### 7. **Heap Sort**
- **Description**: Uses a binary heap data structure to sort elements. It first builds a max heap from the input data, then repeatedly extracts the maximum element from the heap and reconstructs the heap.
- **Time Complexity**: \(O(n \log n)\) in all cases.
- **Example**:
  ```java
  public static void heapSort(int[] array) {
      int n = array.length;

      for (int i = n / 2 - 1; i >= 0; i--)
          heapify(array, n, i);

      for (int i = n - 1; i > 0; i--) {
          int temp = array[0];
          array[0] = array[i];
          array[i] = temp;

          heapify(array, i, 0);
      }
  }

  private static void heapify(int[] array, int n, int i) {
      int largest = i;
      int l = 2 * i + 1;
      int r = 2 * i + 2;

      if (l < n && array[l] > array[largest])
          largest = l;

      if (r < n && array[r] > array[largest])
          largest = r;

      if (largest != i) {
          int swap = array[i];
          array[i] = array[largest];
          array[largest] = swap;

          heapify(array, n, largest);
      }
  }

  int[] array = {12, 11, 13, 5, 6};
  heapSort(array);
  System.out.println(Arrays.toString(array)); // Output: [5, 6, 11, 12, 13]
  ```

#### 8. **Quick Sort**
- **Description**: A divide-and-conquer algorithm that works by selecting a 'pivot' element from the array and partitioning the other elements into two sub-arrays, according to whether they are less than or greater than the pivot.
- **Time Complexity**: Average case is \(O(n \log n)\), but can degrade to \(O(n^2)\) in the worst case.
- **Example**:
  ```java
  public static void quickSort(int[] array, int low, int high) {
      if (low < high) {
          int pi = partition(array, low, high);

          quickSort(array, low, pi - 1);
          quickSort(array, pi + 1, high);
      }
  }

  private static int partition(int[] array, int low, int high) {
      int pivot = array[high];
      int i = (low - 1);
      for (int j = low; j < high; j++) {
          if (array[j] < pivot) {
              i++;

              int temp = array[i];
              array[i] = array[j];
              array[j] = temp;
          }
      }

      int temp = array[i + 1];
      array[i + 1] = array[high];
      array[high] = temp;

      return i + 1;
  }

  int[] array = {10, 7, 8, 9, 1, 5};
  quickSort(array, 0, array.length - 1);
  System.out.println(Arrays.toString(array)); // Output: [1, 5, 7, 8, 9, 10]
  ```

### Key Findings and Insights
- **Built-in Sorting**: Java's `Arrays.sort()` method uses Quicksort for primitive arrays and Merge sort for object arrays.
- **Efficiency**: Merge sort and Quick sort are generally more efficient for large datasets due to their \(O(n \log n)\) time complexity.
- **Simplicity**: Bubble Sort and Insertion Sort are simpler to implement but less efficient for large datasets.
- **Stability**: Merge sort is a stable sort, meaning it maintains the relative order of equal elements, whereas Quick sort is not stable.
- **Optimization**: For arrays with many duplicate keys, using three-way partitioning in Quick sort can reduce the impact of these duplicates and improve performance. Additionally, for small arrays, Insertion Sort can be faster than Quick sort due to lower constant factors.

### Comparison: Quick Sort vs Merge Sort
- **Partitioning Strategy**:
  - **Quick Sort**: Partitions the array into two sub-arrays based on a chosen pivot. This process is repeated recursively.
  - **Merge Sort**: Divides the array into two halves, sorts each half recursively, and then merges the sorted halves.

- **Time Complexity**:
  - **Quick Sort**: Average case is \(O(n \log n)\), but can degrade to \(O(n^2)\) in the worst case (e.g., when the smallest or largest element is always chosen as the pivot). Three-way partitioning can help mitigate this issue.
  - **Merge Sort**: Always \(O(n \log n)\) in all cases.

- **Space Complexity**:
  - **Quick Sort**: In-place sorting algorithm, requiring \(O(\log n)\) additional space for recursion stack.
  - **Merge Sort**: Requires \(O(n)\) additional space for merging.

- **Stability**:
  - **Merge Sort**: Stable sort, maintaining the relative order of equal elements.
  - **Quick Sort**: Not a stable sort, as it does not preserve the relative order of equal elements.

- **Use Cases**:
  - **Quick Sort**: Generally faster in practice for large datasets due to lower overhead and better cache performance. However, for small arrays or arrays with many duplicates, other algorithms like Insertion Sort or optimized Quick Sort with three-way partitioning might be more suitable.
  - **Merge Sort**: Preferred for linked lists and external sorting where stability is required.

This summary covers the essential sorting algorithms in Java, providing examples and insights into their performance and usage. The comparison between Quick Sort and Merge Sort highlights their respective strengths and weaknesses, helping developers choose the appropriate algorithm based on specific requirements.

### Sources:
- [Sorting in Java - GeeksforGeeks](//duckduckgo.com/l/?uddg=https%3A%2F%2Fwww.geeksforgeeks.org%2Fjava%2Fsorting%2Din%2Djava%2F&rut=99840f2b7bcd9350ab9a7a461dac6c87e6398f0fc32409edae19d0103d99a1c0)
- [Sorting Algorithms in Java - Stack Abuse](//duckduckgo.com/l/?uddg=https%3A%2F%2Fstackabuse.com%2Fsorting%2Dalgorithms%2Din%2Djava%2F&rut=850903ed1a0bbcffe016c9c9da33926a0e055745c8e2beeb804b6b37b9be8901)
- [5 Most used Sorting Algorithms in Java (with Code) - FavTutor](//duckduckgo.com/l/?uddg=https%3A%2F%2Ffavtutor.com%2Fblogs%2Fsorting%2Dalgorithms%2Djava&rut=df83530fc7651005ab11fc7fd449bb683018205f484561655ba185563b57b2a3)
- [Quick Sort vs Merge Sort - GeeksforGeeks](//duckduckgo.com/l/?uddg=https%3A%2F%2Fwww.geeksforgeeks.org%2Fdsa%2Fquick%2Dsort%2Dvs%2Dmerge%2Dsort%2F&rut=32864de5c42f6d8c1c12d551dff4beb24fb10f01de9da6a37590ad78721e5bda)
- [Difference Between Quick Sort and Merge Sort (Comparison)](//duckduckgo.com/l/?uddg=https%3A%2F%2Fwww.wscubetech.com%2Fresources%2Fdsa%2Fquick%2Dsort%2Dvs%2Dmerge%2Dsort&rut=1cedcc464dad134f5691bc2c389f8238da47e89858118f7442eea45b3ff71122)
- [Quick Sort vs Merge Sort: Which Algorithm Is Faster?](//duckduckgo.com/l/?uddg=https%3A%2F%2Fwww.mbloging.com%2Fpost%2Fmerge%2Dsort%2Dvs%2Dquick%2Dsort&rut=af8538a0e6673e5cac097d0540f40fe76d0a4e9865ed75a6da518a685a756bb7)
- [Improvement on the Quick Sort Algorithm - GeeksforGeeks](//duckduckgo.com/l/?uddg=https%3A%2F%2Fwww.geeksforgeeks.org%2Fdsa%2Fimprovement%2Don%2Dthe%2Dquick%2Dsort%2Dalgorithm%2F&rut=0c8b704f533084c4b6571d572d38eee773592ddc52644c090354f16252774d27)
- [Quicksort Implementation: Building a Production-Ready Sorting Algorithm ...](//duckduckgo.com/l/?uddg=https%3A%2F%2Fmarkaicode.com%2Fquicksort%2Dproduction%2Dimplementation%2Dguide%2F&rut=8530dc96d12fe185331462d817319db6758c1ffac927b35ed389a9a4ae8d6f57)
- [algorithm - How to optimize quicksort - Stack Overflow](//duckduckgo.com/l/?uddg=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F12454866%2Fhow%2Dto%2Doptimize%2Dquicksort&rut=1f8d90102ff398b00bc6ac399e9f7f793b4ee51cfe296459ceaac6d488ec9d9c)

### Metadata:
- Date: 2025-09-04T18:55:29.237970
- Total Time Taken: 15 sec
- Research Loop Count: 3
- Search Call Count: 3
- LLM Call Count: 6
- LLM Provider: inception
- Model Name: mercury-coder
