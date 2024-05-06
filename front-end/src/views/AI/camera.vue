<template>
  <div class="camera-container">
    <div class="camera-box">
      <video ref="videoRef" autoplay />
      <canvas ref="canvasRef" hidden :width="width" :height="height" />
    </div>
    <el-button class="btn-capture" type="info" @click="handleCapture">Chụp ảnh</el-button>
  </div>
</template>

<script>
export default {
  props: {
    width: {
      type: Number,
      default: 500
    },
    height: {
      type: Number,
      default: 300
    },
    interval: {
      type: Number,
      default: 500
    }
  },
  data() {
    return {
      loop: null
    }
  },

  async mounted() {
    await this.openCamera()
    this.loop = setInterval(() => {
      // this.drawImageFromCamera(this.$refs.videoRef)
      // this.$refs.canvasRef.toBlob((blob) => {
      //   const image = new File([blob], 'face.jpeg', { type: 'image/jpeg' })
      //   this.$emit('capture', image)
      // }, 'image/jpeg')
    }, this.interval)
  },
  beforeDestroy() {
    clearInterval(this.loop)
  },
  methods: {
    handleCapture() {
      this.drawImageFromCamera(this.$refs.videoRef)
      this.$refs.canvasRef.toBlob((blob) => {
        const image = new File([blob], 'face.jpeg', { type: 'image/jpeg' })
        this.$emit('capture', image)
        console.log('image', image)
      }, 'image/jpeg')
    },
    async openCamera() {
      const constraints = {
        audio: false,
        video: true
      }
      try {
        const stream = await navigator.mediaDevices.getUserMedia(constraints)
        this.$refs.videoRef.srcObject = stream
      } catch (error) {
        alert("May the browser didn't support or there is some errors.")
      }
    },
    drawImageFromCamera(video) {
      const ctx = this.$refs.canvasRef.getContext('2d')
      const width = video.offsetWidth
      const height = video.offsetHeight
      ctx.drawImage(video, 0, 0, width, height)
    }
  }
}
</script>

<style scoped lang="scss">
//@import '@styles/mixins';
.camera-container {
  position: relative;
  .camera-box {
    video {
      object-fit: cover;
      transform: rotateY(180deg);
      overflow: hidden;
      border-radius: 4px;
      width: 60rem;
      height: 30rem;
    }
  }
  .btn-capture{
    position: absolute;
    top: 10px;
    left: 10px;
  }
}
</style>
