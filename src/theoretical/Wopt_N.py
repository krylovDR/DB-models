import sys
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import binom
import math


# мат ожидание длины кадра реккурентная формула для геом распределения
def theory_formula(cur_n, cur_w, cur_p):
    mu = np.zeros((cur_n + 1, cur_n + 1), dtype=float)

    for i in range(cur_w - 1, -1, -1):
        n_new = cur_n - i
        w_new = cur_w - i
        sum_val = 0.0

        for j in range(w_new - 1, 0, -1):
            prob = binom.pmf(j, n_new, cur_p)
            sum_val += prob * (1 + mu[w_new - j, n_new - j])

        for j in range(w_new, n_new + 1):
            prob = binom.pmf(j, n_new, cur_p)
            sum_val += prob

        temp = binom.pmf(0, n_new, cur_p)

        sum_val += temp
        sum_val /= (1 - temp)

        mu[w_new, n_new] = sum_val

    return mu[cur_w, cur_n]


# мат ожидание длины кадра для эксп распределения
def exp_distribution(cur_n, cur_w, cur_p):
    sum = 0
    lambda_p = -math.log(1 - cur_p)
    for i in range(cur_n - cur_w + 1, cur_n + 1):
        sum += (1 / i)
    return sum / lambda_p


# вероятности чтения в определенную попытку
def probs(cur_n, cur_w, cur_r):
    q = 1
    for i in range(cur_r):
        if i == cur_n: continue

        q *= (cur_n - cur_w - i) / (cur_n - i)
    p_array = []
    p_array.append(1 - q)
    while p_array[len(p_array) - 1] > 1e-03:
        p_array.append(p_array[len(p_array) - 1] * q)
    return p_array


# средний возраст информации
def aoi_res(p, mu, cur_c):
    res = mu + (cur_c - 1) / 2
    for i in range(2, len(p)):
        res += p[i - 1] * (i - 1) * (mu + cur_c)
    return res


n_max = int(sys.argv[1])
r = int(sys.argv[2])
p_success = float(sys.argv[3])
c = int(sys.argv[4])


opt_w = [1]
n_values = [1]
opt_AoI = [aoi_res(probs(1, 1, r), theory_formula(1, 1, p_success), c)]

for n in range(2, n_max + 1):

    temp_AoI = []
    for w in range (1, n):
        p_cur = probs(n, w, r)
        mu = theory_formula(n, w, p_success)
        # mu2 = exp_distribution(n, w, p_success)
        temp_AoI.append(aoi_res(p_cur, mu, c))

    print("n = " + str(n))


    min_val = min(temp_AoI)
    min_idx = temp_AoI.index(min_val)

    opt_w.append(min_idx + 1)

    opt_AoI.append(min_val)
    n_values.append(n)


with open("results\\Wopt_theor.txt", "w", encoding="utf-8") as f:
    for value in opt_w:
        f.write(str(value) + "\n")

with open("results\\AoIopt_theor.txt", "w", encoding="utf-8") as f:
    for value in opt_AoI:
        f.write(str(value) + "\n")


