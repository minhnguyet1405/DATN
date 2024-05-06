<template>
  <div class="checkin-face">
    <div class="btn-back" @click="handleBackHome" @mouseover="colorIcon = 'white'" @mouseleave="colorIcon='#f37335'">
      <i class="el-icon-back" />
      Trở về
    </div>
    <div class="checkin-face-body">
      <h2>FACE CHECKIN</h2>
      <camera :width="900" :height="500" @capture="captureHandler" />
    </div>
    <img :src="imgBase64" :width="300" :height="200">
  </div>
</template>

<script>
import { checkInFromFace } from '@/api/user.js'
import router from '@/router'
import camera from './camera.vue'
import moment from 'moment'

export default {
  components: { camera },
  data() {
    return {
      imgBase64: null,
      isOpen: false,
      isLock: false,
      user: {
        key: null,
        name: null,
        phone: null
      },
      colorIcon: '#f47b2a'
    }
  },
  watch: {
    isOpen(newValue, oldValue) {
      if (!newValue && oldValue) {
        this.isLock = false
      }
    }
  },
  methods: {
    async captureHandler(image) {
      this.imgBase64 = await this.blobToBase64(image)
      const h = this.$createElement
      checkInFromFace(image)
        .then(({ data }) => {
          this.isLock = true
          this.isOpen = true
          console.log('param', data)
          if (data.key) {
            this.user.key = data.key
            this.user.name = data.name
            this.user.phone = data.phone
            this.$notify({
              title: 'THÀNH CÔNG !',
              message: h('i', { style: 'color: teal' }, this.user.name + '-' + moment().format('HH:mm DD/MM/YYYY')),
              duration: 5000
            })
          } else {
            this.$notify({
              title: 'THẤT BẠI !',
              message: h('i', { style: 'color: teal' }, 'Người dùng chưa đăng ký'),
              duration: 5000
            })
          }
        })
        .catch((_) => {})
        .finally(() => {})
    },
    blobToBase64(blob) {
      return new Promise((resolve, _) => {
        const reader = new FileReader()
        reader.onloadend = () => resolve(reader.result)
        reader.readAsDataURL(blob)
      })
    },
    handleBackHome() {
      router.push('/checkinout/index')
    }
  }
}
</script>
<style scoped lang="scss">
.checkin-face {
  min-height: 100vh;
  height: 100%;
  width: 100vw;
  background: linear-gradient(141deg, #c5f9d7 0%, #f7d486 50.83%, #ff9294 100%);

  .btn-back {
    display: flex;
    width: 200px;
    color: blue;
    font-weight: bold;
    font-size: 2rem;

    background-color: white;
    align-items: center;
    justify-content: center;
    border-radius: 5px;

    position: relative;
    cursor: pointer;
    left: 20px;
    top: 20px;
    padding: 5px 0;

    &:hover {
      color: white;
      background-color: blue;

      svg {
        color: white;
      }
    }
  }
  .checkin-face-body {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    h2 {
      font-size: 4rem;
      color: #ef8721;
    }
  }
}

.modal {
  h2 {
    font-size: 32px;
    line-height: 40px;
    font-weight: 600;
  }

  p {
    font-size: 20px;
    line-height: 28px;
    font-weight: 500;
  }

  button {
    appearance: none;
    outline: none;
    cursor: pointer;
    padding: 1rem 2rem;
    border: 1px solid #fdcbb2;
    color: #f37335;
    font-size: 18px;
    background-color: white;
    border-radius: 5px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    &:hover {
      border-color: #f37335;
    }
  }

  .text-highlight {
    color: blue;
    font-weight: bold;
  }
}
</style>
