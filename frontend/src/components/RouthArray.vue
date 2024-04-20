<template>
  <div class="routh-container">
    <label for="routh-coefficients">Enter the degree of the characteristic equation </label>
    <input type="number" v-model="degree" placeholder="eg. 5"/>

    <div v-for="(value, index) in Array(degree+1)" :key="index">
      <label :for="'coefficient-' + index">S^{{ degree - index }}&nbsp;</label>
      <input type="number" v-model="coefficients[index]" :id="'coefficient-' + index" placeholder="Enter coefficient"/>
    </div>
    <button @click="computeRouth">Calculate</button>

    <div v-if="ansReceived">
      <h3>Computed Routh Array</h3>
      <table style="margin: auto; min-width: 350px">
        <tr v-for="(subArray, index) in ansArr.res" :key="index">
          <td>S^{{ degree - index }}          </td>
          <td v-for="(item, subIndex) in subArray" :key="subIndex">
            {{ item }}
          </td>
        </tr>
      </table>
      <h3>Roots</h3>
      <table style="margin: auto">
      <tr v-for="(root, index) in ansArr.roots" :key="index">
        <td>Root {{ index + 1 }}: </td>
        <td>{{root.real}} + {{root.imaginary}}i</td>
      </tr>
      </table>
      <h3>System State</h3>
      <span>{{ ansArr.status }}</span>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      degree: 0,
      coefficients: [],
      ansArr: {
        res: [],
        roots: [],
        status: ""
      },
      ansReceived: false
    }
  },
  watch: {
    degree(newVal) {
      if (newVal < 0) {
        this.degree = 0;
      } else {
        this.ansReceived = false;
        this.coefficients = [];
      }
    }
  },
  methods: {
    computeRouth() {
      for (let i = 0; i < this.degree; i++) {
        if (this.coefficients[i] === undefined || this.coefficients[i] === null) {
          this.coefficients[i] = 0;
        }
      }
      console.log(this.degree, this.coefficients);
      let coeffArr = this.coefficients;
      coeffArr = JSON.stringify(coeffArr)
      const url = "http://localhost:8080/programming_assignment/computeRouthArray?"
      const params = {
      }
      const query = new URLSearchParams(params)
      const method = "POST"
      const body = coeffArr

      fetch(url+query, {
        method: method,
        body: body,
        headers: {
          'Content-Type': 'application/json'
        },
      })
          .then(res => res.text())
          .then((data) => {
            data = JSON.parse(data);
            this.ansArr.res = data.res;
            this.ansArr.roots = data.roots;
            this.ansArr.status = data.status;
            this.ansReceived = true;
          })
    }
  }
}
</script>

<style scoped lang="scss">
.routh-container {
  margin: 0;
  background: #dddddd;
  position: relative;
  overflow: auto;
  align-content: center;
}

</style>