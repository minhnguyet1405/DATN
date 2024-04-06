export default {
  data() {
    return {
    }
  },
  methods: {

    // ket thuc
    trimData(data) {
      for (const key in data) {
        if (data[key] && typeof data[key] === 'string') {
          data[key] = data[key] ? data[key].trim() : null
        }
      }
      return data
    }
  }
}
