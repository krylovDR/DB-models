import matplotlib.pyplot as plt  # type: ignore
import csv
import sys
import json

# считывание данных для построения из файла .csv
def readFile(idx : int) -> list:
    res = []
    with open(sys.argv[idx], 'r', newline='') as file:
        reader = csv.reader(file)
        for row in reader:
            res.append(float(row[0]))
    return res


def main() -> None:
    values : list = []  # 1 - id данных оси, 2 - данные
    
    # чтение данных из .csv файлов в динамический список
    for i in range(1, len(sys.argv)):
        values.append(readFile(i))

    with open("src\\graphics\\settings.json", "r", encoding="utf-8") as file:
        json_data = json.load(file)

    # построение графика
    fig = plt.subplots()
    for i in range(1, len(values)):
        plt.plot(values[0], values[i], color=json_data["graphics"][i - 1]["color"], 
                                       label=json_data["graphics"][i - 1]["graphic_name"],
                                       linestyle=json_data["graphics"][i - 1]["style"],
                                       marker=json_data["graphics"][i - 1]["marker_style"],
                                       markersize=json_data["graphics"][i - 1]["marker_size"])

    plt.xlabel(json_data["axis_x_name"])
    plt.ylabel(json_data["axis_y_name"])
    plt.title(json_data["title"])
    plt.grid()
    plt.legend()
    plt.show()


if __name__ == "__main__":
    main()

